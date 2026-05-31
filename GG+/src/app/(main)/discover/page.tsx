"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { TrackList } from "@/components/playlist/TrackList";
import { Search, Sparkles, Compass, Loader2 } from "lucide-react";
import { motion } from "framer-motion";

export default function DiscoverPage() {
  const [query, setQuery] = useState("");
  const [useSemantic, setUseSemantic] = useState(true);
  const [submittedQuery, setSubmittedQuery] = useState("");

  // Query search results
  const { data: searchResults = [], isLoading: isSearchLoading } = useQuery({
    queryKey: ["search", submittedQuery, useSemantic],
    queryFn: async () => {
      if (!submittedQuery) return [];
      const res = await fetch(`/api/search?q=${encodeURIComponent(submittedQuery)}&semantic=${useSemantic}`);
      if (!res.ok) throw new Error("Search compilation failed");
      return res.json();
    },
    enabled: !!submittedQuery
  });

  // Query global platform recommendations
  const { data: recommendations = [], isLoading: isRecsLoading } = useQuery({
    queryKey: ["recommendations"],
    queryFn: async () => {
      const res = await fetch("/api/recommendations");
      if (!res.ok) throw new Error("Failed to load recommendations");
      return res.json();
    }
  });

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSubmittedQuery(query);
  };

  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      {/* Top Title */}
      <div>
        <h2 className="text-2xl md:text-3xl font-extrabold text-white font-mono uppercase tracking-wider">
          DISCOVER FREQUENCIES
        </h2>
        <p className="text-xs text-muted-foreground font-mono">
          Explore registered waveforms and scan tracks using standard keyword indexers or dense AI vector semantic controllers.
        </p>
      </div>

      {/* Semantic Search Panel */}
      <form onSubmit={handleSearchSubmit} className="bg-cyber-dark/50 p-6 rounded-xl border border-cyber-gray/40 glassmorphism space-y-4">
        <div className="flex flex-col md:flex-row gap-4 items-center">
          <div className="relative flex-1 w-full">
            <Search className="w-4.5 h-4.5 text-muted-foreground absolute left-3.5 top-1/2 -translate-y-1/2" />
            <Input
              type="text"
              placeholder="e.g. moody cyberpunk beats for driving, high energy rock vocals..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="pl-10 h-11"
            />
          </div>
          <div className="flex items-center gap-3 w-full md:w-auto justify-between">
            <button
              type="button"
              onClick={() => setUseSemantic(!useSemantic)}
              className={`px-4 py-2.5 rounded-lg border font-mono text-xs uppercase tracking-wider transition-all duration-300 flex items-center gap-2 ${
                useSemantic 
                  ? "border-cyber-neon/40 text-cyber-neon bg-cyber-neon/5 hover:bg-cyber-neon/15" 
                  : "border-cyber-gray/40 text-muted-foreground bg-transparent"
              }`}
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>{useSemantic ? "Semantic Embeddings" : "Keyword Match"}</span>
            </button>
            <Button type="submit" className="h-11 px-6 w-full md:w-auto">
              SCAN INDEX
            </Button>
          </div>
        </div>
      </form>

      {/* Search results renderer */}
      {submittedQuery && (
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-cyber-pink tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
            SCAN RESULTS // SIMILARITY SCORES
          </h3>

          {isSearchLoading ? (
            <div className="flex flex-col items-center justify-center py-12 space-y-2">
              <Loader2 className="w-7 h-7 text-cyber-pink animate-spin" />
              <p className="text-[10px] font-mono text-muted-foreground uppercase tracking-widest">
                SCROLLING HYBRID MATRICES...
              </p>
            </div>
          ) : searchResults.length === 0 ? (
            <p className="text-xs font-mono text-muted-foreground text-center py-8">
              No matching acoustic profiles registered in current dataset index.
            </p>
          ) : (
            <div className="bg-cyber-dark/40 rounded-xl border border-cyber-gray/25 p-4">
              <TrackList tracks={searchResults} showHeaders={false} />
            </div>
          )}
        </div>
      )}

      {/* Recommendations recommendations */}
      <div className="space-y-4">
        <h3 className="text-sm font-bold text-cyber-neon tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
          HYBRID AI RECOMMENDATIONS FOR YOU
        </h3>

        {isRecsLoading ? (
          <div className="flex flex-col items-center justify-center py-12 space-y-2">
            <Loader2 className="w-7 h-7 text-cyber-neon animate-spin" />
            <p className="text-[10px] font-mono text-muted-foreground uppercase tracking-widest">
              PARSING PREFERENCES VECTOR...
            </p>
          </div>
        ) : (
          <div className="bg-cyber-dark/40 rounded-xl border border-cyber-gray/25 p-4">
            <TrackList tracks={recommendations} showHeaders={true} />
          </div>
        )}
      </div>
    </div>
  );
}
