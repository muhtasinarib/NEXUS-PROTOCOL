import { NextRequest, NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { AnalyticsService } from "@/lib/services/analytics.service";
import { logger } from "@/lib/logger";

export const GET = auth(async (req) => {
  if (req.auth?.user?.role !== "ADMIN") {
    return NextResponse.json({ error: "Forbidden" }, { status: 403 });
  }

  try {
    const metrics = await AnalyticsService.getAggregatedMetrics();
    return NextResponse.json(metrics);
  } catch (e) {
    logger.error("Admin analytical aggregation failed", e);
    return NextResponse.json({ error: "Server error" }, { status: 500 });
  }
}) as any;
