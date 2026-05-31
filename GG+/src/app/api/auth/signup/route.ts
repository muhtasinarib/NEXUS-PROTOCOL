import { NextRequest, NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import * as bcrypt from "bcryptjs";
import { signupSchema } from "@/lib/validators";
import { logger } from "@/lib/logger";

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const validated = signupSchema.safeParse(body);

    if (!validated.success) {
      return NextResponse.json({ error: "Invalid inputs schemas." }, { status: 400 });
    }

    const { name, email, password } = validated.data;

    // Check duplicate
    const existing = await prisma.user.findUnique({ where: { email } });
    if (existing) {
      return NextResponse.json({ error: "EMAIL KEY ALREADY REGISTERED." }, { status: 409 });
    }

    // Salt and hash passwords
    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);

    const user = await prisma.user.create({
      data: {
        name,
        email,
        passwordHash,
        role: "USER"
      }
    });

    logger.info(`Successfully registered new Decibel user: ${user.email}`);

    return NextResponse.json({ success: true, userId: user.id }, { status: 201 });
  } catch (e) {
    logger.error("Signup handler registry error", e);
    return NextResponse.json({ error: "Server registration failure." }, { status: 500 });
  }
}
