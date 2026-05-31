import { NextRequest, NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { logger } from "@/lib/logger";

export const GET = auth(async (req) => {
  if (req.auth?.user?.role !== "ADMIN") {
    return NextResponse.json({ error: "Forbidden" }, { status: 403 });
  }

  try {
    const users = await prisma.user.findMany({
      include: {
        _count: {
          select: { playlists: true, likes: true }
        }
      },
      orderBy: { createdAt: "desc" }
    });

    return NextResponse.json(users);
  } catch (e) {
    logger.error("Admin user list compilation failed", e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;

export const PATCH = auth(async (req) => {
  if (req.auth?.user?.role !== "ADMIN") {
    return NextResponse.json({ error: "Forbidden" }, { status: 403 });
  }

  try {
    const { userId, role } = await req.json();
    if (!userId || !role) {
      return NextResponse.json({ error: "UserId and Role are required" }, { status: 400 });
    }

    const updated = await prisma.user.update({
      where: { id: userId },
      data: { role }
    });

    return NextResponse.json({ success: true, user: updated });
  } catch (e) {
    logger.error("Admin user role modification failed", e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;

export const DELETE = auth(async (req) => {
  if (req.auth?.user?.role !== "ADMIN") {
    return NextResponse.json({ error: "Forbidden" }, { status: 403 });
  }

  const { searchParams } = new URL(req.url);
  const userId = searchParams.get("userId");

  if (!userId) {
    return NextResponse.json({ error: "UserId parameter is required" }, { status: 400 });
  }

  try {
    await prisma.user.delete({ where: { id: userId } });
    return NextResponse.json({ success: true, message: "User account deleted successfully" });
  } catch (e) {
    logger.error(`Admin user deletion failed for ${userId}`, e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;
