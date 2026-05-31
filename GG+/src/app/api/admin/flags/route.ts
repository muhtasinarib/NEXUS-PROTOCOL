import { NextRequest, NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { logger } from "@/lib/logger";

export const GET = auth(async (req) => {
  if (req.auth?.user?.role !== "ADMIN") {
    return NextResponse.json({ error: "Forbidden" }, { status: 403 });
  }

  try {
    const flags = await prisma.featureFlag.findMany({
      orderBy: { name: "asc" }
    });
    return NextResponse.json(flags);
  } catch (e) {
    logger.error("Admin feature flag retrieval failed", e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;

export const PATCH = auth(async (req) => {
  if (req.auth?.user?.role !== "ADMIN") {
    return NextResponse.json({ error: "Forbidden" }, { status: 403 });
  }

  try {
    const { name, isActive } = await req.json();
    if (!name) {
      return NextResponse.json({ error: "Flag name is required" }, { status: 400 });
    }

    const flag = await prisma.featureFlag.update({
      where: { name },
      data: { isActive }
    });

    return NextResponse.json({ success: true, flag });
  } catch (e) {
    logger.error(`Admin feature flag modification failed for ${e}`, e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;
