"use client";

import { useSession } from "next-auth/react";
import { useQuery } from "@tanstack/react-query";
import { User, Music, Heart, Shield, Disc, Settings, Loader2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { TrackList } from "@/components/playlist/TrackList";

export default function ProfilePage() {
  const { data: session } = useSession();

  // Query liked tracks list
  const { data: playlists = [] } = useQuery({
    queryKey: ["playlists"],
    queryFn: async () => {
      const res = await fetch("/api/playlists");
      if (!res.ok) throw new Error("Playlists failed");
      return res.json();
    }
  });

  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      {/* Top Banner Cover */}
      <div className="h-48 rounded-xl bg-gradient-to-r from-cyber-neon/15 via-accent/15 to-cyber-pink/15 border border-cyber-gray/30 relative overflow-hidden flex items-end p-6">
        <div className="absolute inset-0 cyber-grid opacity-10" />
        
        <div className="flex flex-col sm:flex-row items-center gap-4 relative z-10">
          {/* Glowing Avatar */}
          <div className="w-20 h-20 rounded-full border-2 border-cyber-neon shadow-[0_0_15px_rgba(0,240,255,0.4)] overflow-hidden bg-cyber-dark flex items-center justify-center flex-shrink-0">
            {session?.user?.image ? (
              <img src={session.user.image} alt={session.user.name || "Operator"} className="w-full h-full object-cover" />
            ) : (
              <User className="w-8 h-8 text-cyber-neon" />
            )}
          </div>
          
          <div className="text-center sm:text-left space-y-1">
            <h2 className="text-xl md:text-2xl font-extrabold text-white font-mono uppercase tracking-wider">
              {session?.user?.name || "Anonymous Runner"}
            </h2>
            <p className="text-xs text-muted-foreground font-mono">
              ROLE ID // {session?.user?.role || "USER"}
            </p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Statistics block */}
        <div className="space-y-6">
          <h3 className="text-sm font-bold text-cyber-neon tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
            GRID PARAMETERS
          </h3>

          <Card>
            <CardContent className="p-6 space-y-4">
              <div className="flex items-center justify-between border-b border-cyber-gray/10 pb-3">
                <span className="text-xs font-mono text-muted-foreground">TOTAL FREQUENCIES SYNTHED</span>
                <span className="font-mono text-sm text-white font-bold">{playlists.length}</span>
              </div>
              <div className="flex items-center justify-between border-b border-cyber-gray/10 pb-3">
                <span className="text-xs font-mono text-muted-foreground">ACTIVE CONNECTIONS</span>
                <span className="font-mono text-sm text-white font-bold">1</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-xs font-mono text-muted-foreground">ROOT NODE PRIVILEGES</span>
                <span className="font-mono text-xs text-cyber-pink font-bold">VERIFIED</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Playlists block */}
        <div className="lg:col-span-2 space-y-6">
          <h3 className="text-sm font-bold text-cyber-pink tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
            RECENT CODESETS
          </h3>

          <div className="space-y-4">
            {playlists.slice(0, 3).map((pl) => (
              <div key={pl.id} className="flex items-center gap-4 p-4 rounded-lg bg-cyber-dark/50 border border-cyber-gray/25 hover:border-cyber-neon/30 transition-all duration-300">
                <div className="w-12 h-12 rounded bg-cyber-gray flex items-center justify-center border border-cyber-gray/40 overflow-hidden">
                  {pl.coverUrl ? (
                    <img src={pl.coverUrl} alt={pl.name} className="w-full h-full object-cover" />
                  ) : (
                    <Music className="w-5 h-5 text-cyber-neon" />
                  )}
                </div>
                <div className="truncate flex-1">
                  <span className="block text-sm font-extrabold text-white font-mono uppercase truncate">{pl.name}</span>
                  <span className="block text-[10px] text-muted-foreground truncate">{pl.description || "Synthed decibel waveform."}</span>
                </div>
                <span className="font-mono text-[10px] text-cyber-pink border border-cyber-pink/20 bg-cyber-pink/5 px-2 py-0.5 rounded uppercase">
                  {pl.vibe || "lofi"}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
