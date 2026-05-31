"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input, Label } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Music, AlertTriangle, Loader2, CheckCircle2 } from "lucide-react";
import { motion } from "framer-motion";

export default function SignupPage() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [isLoading, setLoading] = useState(false);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !email || !password) return;

    setLoading(true);
    setErrorMsg("");
    setSuccessMsg("");

    try {
      const res = await fetch("/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password })
      });

      const data = await res.json();
      if (!res.ok) {
        setErrorMsg(data.error || "REGISTRATION FAILURE. VERIFY CORE PARAMETERS.");
      } else {
        setSuccessMsg("CORE REGISTRY SUCCESSFULLY COMPLETED. ACCESS INITIALIZED.");
        setTimeout(() => {
          router.push("/onboarding"); // take user directly to onboarding flow
        }, 1500);
      }
    } catch {
      setErrorMsg("NETWORK DECRYPTION ERROR. NODE REGISTER FAIL.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center relative p-6 bg-[#040407]">
      <div className="absolute inset-0 cyber-grid opacity-15 pointer-events-none" />

      <Card className="max-w-md w-full border border-cyber-neon/40 neon-border bg-cyber-dark/80 p-6 glassmorphism relative overflow-hidden">
        {/* Glowing top aesthetic line */}
        <div className="absolute top-0 left-0 w-full h-[2.5px] bg-gradient-to-r from-cyber-neon via-accent to-cyber-pink" />

        <CardContent className="p-4 space-y-6">
          {/* Header titles */}
          <div className="text-center space-y-2">
            <div className="w-12 h-12 rounded-xl bg-gradient-to-tr from-cyber-neon to-cyber-pink flex items-center justify-center mx-auto shadow-lg shadow-cyber-neon/15">
              <Music className="w-6 h-6 text-black" />
            </div>
            <h3 className="font-extrabold text-xl text-white font-mono uppercase tracking-widest neon-text-cyan">
              REGISTER NEW OPERATOR
            </h3>
            <p className="text-[10px] text-muted-foreground font-mono uppercase tracking-wider">
              Sync credentials with Decibel database networks.
            </p>
          </div>

          {/* Validation indicators */}
          {errorMsg && (
            <div className="flex items-center gap-2 border border-cyber-pink/30 bg-cyber-pink/5 p-3 rounded text-cyber-pink text-xs font-mono">
              <AlertTriangle className="w-4 h-4 animate-pulse" />
              <span>{errorMsg}</span>
            </div>
          )}

          {successMsg && (
            <div className="flex items-center gap-2 border border-green-500/30 bg-green-500/5 p-3 rounded text-green-400 text-xs font-mono">
              <CheckCircle2 className="w-4 h-4 animate-bounce" />
              <span>{successMsg}</span>
            </div>
          )}

          {/* Registration forms */}
          <form onSubmit={onSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="name">OPERATOR NAME</Label>
              <Input
                id="name"
                type="text"
                required
                placeholder="e.g. Neon Runner"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="email">EMAIL SHELL</Label>
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
                placeholder="Min. 6 characters"
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
                <span>REGISTER CODESETS SYSTEM</span>
              )}
            </Button>
          </form>

          {/* Signin redirect links */}
          <p className="text-[10px] text-center text-muted-foreground font-mono uppercase tracking-widest pt-2">
            Active core credentials registered?{" "}
            <Link href="/login" className="text-cyber-neon hover:underline font-bold">
              Access Terminal
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
