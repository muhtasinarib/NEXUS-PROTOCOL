"use client";

import { useQuery } from "@tanstack/react-query";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/table";
import { BarChart3, TrendingUp, Music, Sparkles, Loader2 } from "lucide-react";

export default function AnalyticsPage() {
  const { data: stats = {}, isLoading } = useQuery({
    queryKey: ["dashboard-metrics"],
    queryFn: async () => {
      const res = await fetch("/api/analytics");
      if (!res.ok) throw new Error("Analytics failed");
      return res.json();
    }
  });

  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      {/* Page Heading */}
      <div>
        <h2 className="text-2xl md:text-3xl font-extrabold text-white font-mono uppercase tracking-wider">
          NEURAL METRICS ENGINE
        </h2>
        <p className="text-xs text-muted-foreground font-mono">
          System telemetry showing active mood tracking and top frequency ranges.
        </p>
      </div>

      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-20 space-y-2">
          <Loader2 className="w-8 h-8 text-cyber-neon animate-spin" />
          <p className="text-xs font-mono text-muted-foreground uppercase tracking-widest">
            PARSING NEURAL TELEMETRY...
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Favorite Moods statistics */}
          <Card>
            <CardContent className="p-6 space-y-6">
              <div className="flex items-center gap-2 border-b border-cyber-gray/20 pb-3">
                <Sparkles className="w-5 h-5 text-cyber-neon" />
                <h3 className="text-sm font-extrabold text-white font-mono uppercase tracking-widest">
                  POPULAR NEURAL EMOTIONS
                </h3>
              </div>

              <div className="space-y-4">
                {(stats.popularMoods || []).map((mood: any, idx: number) => {
                  const maxCount = Math.max(...(stats.popularMoods || []).map((m: any) => m.count));
                  const percentage = ((mood.count / (maxCount || 1)) * 100).toFixed(0);

                  return (
                    <div key={idx} className="space-y-1.5">
                      <div className="flex justify-between items-center text-xs font-mono">
                        <span className="text-white uppercase font-bold">{mood.name}</span>
                        <span className="text-cyber-neon font-bold">{mood.count} gens</span>
                      </div>
                      <Progress value={parseInt(percentage, 10)} />
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          {/* Favorite acoustic vibes (synthwave, lo-fi, progressive house, orchestral, etc.) */}
          <Card>
            <CardContent className="p-6 space-y-6">
              <div className="flex items-center gap-2 border-b border-cyber-gray/20 pb-3">
                <Music className="w-5 h-5 text-cyber-pink" />
                <h3 className="text-sm font-extrabold text-white font-mono uppercase tracking-widest">
                  ACOUSTIC SPECTRUM INDEX
                </h3>
              </div>

              <div className="space-y-4">
                {[
                  { name: "darksynth", percentage: 85, color: "text-cyber-pink" },
                  { name: "lofi ambient", percentage: 65, color: "text-cyber-neon" },
                  { name: "synthwave", percentage: 50, color: "text-accent" },
                  { name: "minimal electronic", percentage: 35, color: "text-yellow-400" }
                ].map((item, idx) => (
                  <div key={idx} className="space-y-1.5">
                    <div className="flex justify-between items-center text-xs font-mono">
                      <span className="text-white uppercase font-bold">{item.name}</span>
                      <span className={`${item.color} font-bold`}>{item.percentage}% match</span>
                    </div>
                    <Progress value={item.percentage} />
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
