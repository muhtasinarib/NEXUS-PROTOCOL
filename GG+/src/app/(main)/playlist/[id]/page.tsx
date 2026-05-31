"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { TrackList } from "@/components/playlist/TrackList";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Music, Sparkles, Sliders, Calendar, ArrowLeft, Loader2, RefreshCw } from "lucide-react";
import Link from "next/link";

interface PlaylistDetailPageProps {
  params: { id: string };
}

export default function PlaylistDetailPage({ params }: PlaylistDetailPageProps) {
  const { id } = params;
  const queryClient = useQueryClient();
  const [feedback, setFeedback] = useState("");

  // 1. Query Playlist details
  const { data: playlist = null, isLoading, error } = useQuery({
    queryKey: ["playlist", id],
    queryFn: async () => {
      const res = await fetch(`/api/playlists/${id}`);
      if (!res.ok) throw new Error("Failed to load playlist details");
      return res.json();
    }
  });

  // 2. Query Hybrid recommendations based on this playlist seed
  const { data: recommendations = [], isLoading: isRecsLoading } = useQuery({
    queryKey: ["playlist-recommendations", id],
    queryFn: async () => {
      const res = await fetch(`/api/recommendations?playlistId=${id}&limit=5`);
      if (!res.ok) throw new Error("Failed to load recommendations");
      return res.json();
    },
    enabled: !!playlist
  });

  // 3. AI Feedback Modifier Mutation
  const modifierMutation = useMutation({
    mutationFn: async (promptText: string) => {
      const res = await fetch(`/api/playlists/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ feedbackPrompt: promptText })
      });
      if (!res.ok) throw new Error("Feedback modification failed");
      return res.json();
    },
    onSuccess: () => {
      setFeedback("");
      queryClient.invalidateQueries({ queryKey: ["playlist", id] });
      queryClient.invalidateQueries({ queryKey: ["playlist-recommendations", id] });
    }
  });

  const handleFeedbackSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!feedback.trim()) return;
    modifierMutation.mutate(feedback);
  };

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-32 space-y-3">
        <Loader2 className="w-8 h-8 text-cyber-neon animate-spin" />
        <p className="text-xs font-mono text-muted-foreground uppercase tracking-widest">
          CONNECTING WAVEFORM DATASET...
        </p>
      </div>
    );
  }

  if (error || !playlist) {
    return (
      <div className="text-center py-20">
        <p className="text-sm font-mono text-red-400 uppercase tracking-wider">
          FATAL ERROR // WAVEFORM RECOGNITION FAIL
        </p>
        <Link href="/dashboard" className="mt-4 inline-block">
          <Button variant="outline">RETURN TERMINAL</Button>
        </Link>
      </div>
    );
  }

  const creationDate = new Date(playlist.createdAt).toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric"
  });

  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      {/* Back button */}
      <Link href="/dashboard" className="inline-flex items-center gap-2 text-xs font-mono text-muted-foreground hover:text-white transition-colors duration-300">
        <ArrowLeft className="w-4 h-4" />
        <span>BACK TERMINAL</span>
      </Link>

      {/* Playlist Header block */}
      <div className="flex flex-col md:flex-row gap-6 items-center md:items-end">
        {/* Cover art */}
        <div className="w-48 h-48 rounded-xl bg-cyber-gray overflow-hidden border border-cyber-gray/45 flex-shrink-0 shadow-lg relative group">
          {playlist.coverUrl ? (
            <img src={playlist.coverUrl} alt={playlist.name} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <Music className="w-16 h-16 text-cyber-neon/40" />
            </div>
          )}
        </div>

        {/* Text Details metadata */}
        <div className="space-y-3 flex-1 text-center md:text-left">
          <div className="inline-flex items-center gap-2 border border-cyber-neon/30 bg-cyber-neon/5 px-2.5 py-0.5 rounded font-mono text-[9px] text-cyber-neon uppercase tracking-wider">
            {playlist.vibe || "lofi VIBE"}
          </div>
          
          <h2 className="text-2xl md:text-4xl font-extrabold text-white font-mono uppercase tracking-wide neon-text-cyan">
            {playlist.name}
          </h2>

          <p className="text-sm text-muted-foreground leading-relaxed max-w-2xl">
            {playlist.description || "Generated AI acoustic compiled decibels waveform."}
          </p>

          {/* Quick specs */}
          <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 text-xs font-mono text-muted-foreground pt-1">
            <span className="flex items-center gap-1">
              <Calendar className="w-3.5 h-3.5" />
              {creationDate}
            </span>
            <span className="w-1.5 h-1.5 rounded-full bg-cyber-gray" />
            <span className="flex items-center gap-1">
              <Sliders className="w-3.5 h-3.5 text-cyber-pink" />
              {(playlist.energyLevel * 100).toFixed(0)}% Energy Range
            </span>
            <span className="w-1.5 h-1.5 rounded-full bg-cyber-gray" />
            <span className="text-white font-bold">{playlist.tracks?.length || 0} Sequences</span>
          </div>
        </div>
      </div>

      {/* Dynamic Interactive AI Feedback Modifier Bar */}
      <Card className="border border-cyber-neon/40 bg-cyber-neon/5 relative overflow-hidden">
        <CardContent className="p-5">
          <form onSubmit={handleFeedbackSubmit} className="space-y-3">
            <div className="flex justify-between items-center text-xs font-mono">
              <span className="text-cyber-neon font-bold flex items-center gap-1.5">
                <Sparkles className="w-4 h-4 text-cyber-neon animate-pulse" />
                NEURAL PLAYLIST MODIFIER CHANNELS
              </span>
              <span className="text-[10px] text-muted-foreground uppercase">GPT-4 PROMPT EDIT</span>
            </div>

            <div className="flex flex-col sm:flex-row gap-3">
              <Input
                type="text"
                placeholder="e.g. Make this darker, Add more high-energy tracks, Remove soft beats..."
                value={feedback}
                onChange={(e) => setFeedback(e.target.value)}
                disabled={modifierMutation.isPending}
                className="flex-1 bg-cyber-black/50"
              />
              <Button 
                type="submit" 
                variant="cyber"
                disabled={modifierMutation.isPending}
                className="min-w-[120px] flex items-center justify-center gap-1.5"
              >
                {modifierMutation.isPending ? (
                  <RefreshCw className="w-4 h-4 animate-spin text-black" />
                ) : (
                  <span>REFINE CODE</span>
                )}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Playlist track records */}
      <div className="space-y-4">
        <h3 className="text-sm font-bold text-white tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
          COMPILATION WAVEFORMS
        </h3>
        
        <div className="bg-cyber-dark/45 rounded-xl border border-cyber-gray/25 p-4">
          <TrackList tracks={playlist.tracks} playlistId={id} showHeaders={true} />
        </div>
      </div>

      {/* Suggested next track recommendation matches */}
      <div className="space-y-4 pt-4">
        <h3 className="text-sm font-bold text-cyber-pink tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
          COMPATIBLE RECOMMENDED FREQUENCIES
        </h3>

        {isRecsLoading ? (
          <div className="flex justify-center py-6">
            <Loader2 className="w-6 h-6 animate-spin text-cyber-pink" />
          </div>
        ) : (
          <div className="bg-cyber-dark/45 rounded-xl border border-cyber-gray/25 p-4">
            <TrackList tracks={recommendations} showHeaders={false} />
          </div>
        )}
      </div>
    </div>
  );
}
