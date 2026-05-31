import { NextRequest, NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { logger } from "@/lib/logger";

export const GET = auth(async (req) => {
  const userId = req.auth?.user?.id;
  if (!userId) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  try {
    const historyCount = await prisma.listeningHistory.count({ where: { userId } });
    const likedCount = await prisma.like.count({ where: { userId } });
    const playlistCount = await prisma.playlist.count({ where: { userId } });

    // Aggregates of most generated moods
    const generatedEvents = await prisma.analytics.findMany({
      where: { userId, eventType: "PLAYLIST_GENERATED" },
      select: { metadata: true }
    });

    const moodCounts: Record<string, number> = {};
    for (const item of generatedEvents) {
      if (item.metadata) {
        try {
          const parsed = typeof item.metadata === "string" ? JSON.parse(item.metadata) : item.metadata;
          const mood = parsed.mood || "custom";
          moodCounts[mood] = (moodCounts[mood] || 0) + 1;
        } catch {
          // ignore
        }
      }
    }

    const popularMoods = Object.entries(moodCounts)
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);

    return NextResponse.json({
      historyCount,
      likedCount,
      playlistCount,
      popularMoods
    });
  } catch (e) {
    logger.error("Failed to compile user analytics data", e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;

export const POST = auth(async (req) => {
  const userId = req.auth?.user?.id || null;

  try {
    const { eventType, metadata } = await req.json();
    if (!eventType) {
      return NextResponse.json({ error: "EventType is required" }, { status: 400 });
    }

    const event = await prisma.analytics.create({
      data: {
        userId,
        eventType,
        metadata: metadata ? JSON.stringify(metadata) : null
      }
    });

    return NextResponse.json({ success: true, event });
  } catch (e) {
    logger.error("Failed to record client interaction analytic event", e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;
