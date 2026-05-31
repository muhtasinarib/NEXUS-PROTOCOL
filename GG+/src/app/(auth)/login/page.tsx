"use client";

import { useState } from "react";
import { signIn } from "next-auth/react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input, Label } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Music, AlertTriangle, Loader2 } from "lucide-react";
import { motion } from "framer-motion";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setLoading] = useState(false);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) return;
    
    setLoading(true);
    setErrorMsg("");

    try {
      const res = await signIn("credentials", {
        email,
        password,
        redirect: false
      });

      if (res?.error) {
        setErrorMsg("AUTHENTICATION FAILED. INCORRECT OPERATOR SIGNATURES.");
      } else {
        router.push("/dashboard");
      }
    } catch {
      setErrorMsg("NETWORK DECRYPTION ERROR. TERMINAL TIMEOUT.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center relative p-6 bg-[#040407]">
      <div className="absolute inset-0 cyber-grid opacity-15 pointer-events-none" />

      <Card className="max-w-md w-full border border-cyber-neon/40 neon-border bg-cyber-dark/80 p-6 glassmorphism relative overflow-hidden">
        {/* Glowing top aesthetic stripe */}
        <div className="absolute top-0 left-0 w-full h-[2.5px] bg-gradient-to-r from-cyber-neon via-accent to-cyber-pink" />

        <CardContent className="p-4 space-y-6">
          {/* Logo Heading */}
          <div className="text-center space-y-2">
            <div className="w-12 h-12 rounded-xl bg-gradient-to-tr from-cyber-neon to-cyber-pink flex items-center justify-center mx-auto shadow-lg shadow-cyber-neon/15">
              <Music className="w-6 h-6 text-black" />
            </div>
            <h3 className="font-extrabold text-xl text-white font-mono uppercase tracking-widest neon-text-cyan">
              ACCESS TERMINAL
            </h3>
            <p className="text-[10px] text-muted-foreground font-mono uppercase tracking-wider">
              Enter decibel operator codesets to connect.
            </p>
          </div>

          {/* Validation Alert */}
          {errorMsg && (
            <div className="flex items-center gap-2 border border-cyber-pink/30 bg-cyber-pink/5 p-3 rounded text-cyber-pink text-xs font-mono">
              <AlertTriangle className="w-4 h-4 text-cyber-pink animate-pulse" />
              <span>{errorMsg}</span>
            </div>
          )}

          {/* Form fields */}
          <form onSubmit={onSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="email">EMAIL INDEX</Label>
              <Input
                id="email"
                type="email"
                required
                placeholder="e.g. operator@cyber.fm"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="password">SECRET DECRYPTION CODE</Label>
              <Input
                id="password"
                type="password"
                required
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <Button
              type="submit"
              disabled={isLoading}
              variant="cyber"
              className="w-full mt-4 h-11 flex items-center justify-center gap-2"
            >
              {isLoading ? (
                <Loader2 className="w-4 h-4 animate-spin text-black" />
              ) : (
                <span>INITIATE OPERATOR SESSION</span>
              )}
            </Button>
          </form>

          {/* Provider sign-ins */}
          <div className="relative flex py-2 items-center">
            <div className="flex-grow border-t border-cyber-gray/20"></div>
            <span className="flex-shrink mx-4 text-[9px] font-mono text-muted-foreground uppercase tracking-widest">
              SECONDARY CHANNELS
            </span>
            <div className="flex-grow border-t border-cyber-gray/20"></div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Button
              type="button"
              variant="outline"
              onClick={() => signIn("spotify", { callbackUrl: "/dashboard" })}
              className="text-xs"
            >
              SPOTIFY LINK
            </Button>
            <Button
              type="button"
              variant="outline"
              onClick={() => signIn("google", { callbackUrl: "/dashboard" })}
              className="text-xs"
            >
              GOOGLE CORE
            </Button>
          </div>

          {/* Link to signup */}
          <p className="text-[10px] text-center text-muted-foreground font-mono uppercase tracking-widest pt-2">
            No active core registered?{" "}
            <Link href="/signup" className="text-cyber-neon hover:underline font-bold">
              Register here
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
