"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { PlaylistGrid } from "@/components/playlist/PlaylistGrid";
import { Sparkles, Music, Heart, BarChart3, Disc, Loader2 } from "lucide-react";
import { motion } from "framer-motion";

export default function Dashboard() {
  // 1. Query all user playlists
  const { data: playlists = [], isLoading: isPlaylistsLoading } = useQuery({
    queryKey: ["playlists"],
    queryFn: async () => {
      const res = await fetch("/api/playlists");
      if (!res.ok) throw new Error("Failed to load user playlists");
      return res.json();
    }
  });

  // 2. Query personal aggregates
  const { data: stats = {}, isLoading: isStatsLoading } = useQuery({
    queryKey: ["dashboard-metrics"],
    queryFn: async () => {
      const res = await fetch("/api/analytics");
      if (!res.ok) throw new Error("Failed to load user analytics");
      return res.json();
    }
  });

  const cards = [
    { title: "Active Waveforms", count: playlists.length || 0, icon: Music, color: "text-cyber-neon" },
    { title: "Liked Waveforms", count: stats.likedCount || 0, icon: Heart, color: "text-cyber-pink" },
    { title: "Neural Stream History", count: stats.historyCount || 0, icon: Disc, color: "text-accent" }
  ];

  return (
    <div className="space-y-8 relative">
      {/* Dashboard Top Heading */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl md:text-3xl font-extrabold text-white font-mono uppercase tracking-wider">
            DECIBEL // TERMINAL
          </h2>
          <p className="text-xs text-muted-foreground font-mono">
            Welcome back, Operator. System grids are online.
          </p>
        </div>
        <Link href="/generate">
          <Button variant="cyber" className="flex items-center gap-2 group">
            <Sparkles className="w-4.5 h-4.5 group-hover:animate-pulse" />
            <span>GENERATE NEW AI SEQUENCE</span>
          </Button>
        </Link>
      </div>

      {/* Aggregate Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {cards.map((card, idx) => (
          <motion.div
            key={idx}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: idx * 0.1 }}
            className="p-6 rounded-lg border border-cyber-gray/25 bg-cyber-dark/65 glassmorphism flex items-center justify-between"
          >
            <div className="space-y-1">
              <span className="text-xs font-mono text-muted-foreground uppercase tracking-wider block">
                {card.title}
              </span>
              {isStatsLoading ? (
                <Loader2 className="w-5 h-5 animate-spin text-muted-foreground" />
              ) : (
                <span className="text-2xl font-bold font-mono text-white block">
                  {card.count}
                </span>
              )}
            </div>
            <card.icon className={`w-8 h-8 ${card.color} opacity-75`} />
          </motion.div>
        ))}
      </div>

      {/* Primary generated playlists grid */}
      <div className="space-y-4">
        <h3 className="text-sm font-bold text-cyber-neon tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
          COMPILATION REGISTRY
        </h3>
        
        {isPlaylistsLoading ? (
          <div className="flex flex-col items-center justify-center py-20 space-y-3">
            <Loader2 className="w-8 h-8 text-cyber-neon animate-spin" />
            <p className="text-xs font-mono text-muted-foreground uppercase tracking-widest">
              SCANNING REGISTERED SEQUENCES...
            </p>
          </div>
        ) : (
          <PlaylistGrid playlists={playlists} />
        )}
      </div>
    </div>
  );
}
