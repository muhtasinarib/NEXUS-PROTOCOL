"use client";

import { GenerateForm } from "@/components/playlist/GenerateForm";
import { Sparkles } from "lucide-react";

export default function GeneratePage() {
  return (
    <div className="space-y-8 max-w-4xl mx-auto">
      {/* Title Header */}
      <div className="text-center space-y-2">
        <div className="inline-flex items-center gap-2 border border-cyber-neon/20 bg-cyber-neon/5 px-3 py-1 rounded-full font-mono text-[9px] text-cyber-neon tracking-widest uppercase mb-1">
          <Sparkles className="w-3 h-3 animate-spin" />
          DECIBEL NEURAL DECRYPTOR
        </div>
        <h2 className="text-2xl md:text-3xl font-extrabold text-white font-mono uppercase tracking-wider">
          SYNTHESIZE SONIC WAVEFORMS
        </h2>
        <p className="text-xs text-muted-foreground font-mono max-w-xl mx-auto">
          AI decrypts your raw intent parameters (mood prompt, genres, vibe details) and synthesizes a fully customized premium playlist.
        </p>
      </div>

      {/* Main generation parameters form */}
      <GenerateForm />
    </div>
  );
}
