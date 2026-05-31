"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Shield, Users, Power, Trash2, CheckCircle2, AlertTriangle, Loader2 } from "lucide-react";

export default function AdminPortal() {
  const queryClient = useQueryClient();

  // Query platform analytics
  const { data: metrics = {}, isLoading: isMetricsLoading } = useQuery({
    queryKey: ["admin-metrics"],
    queryFn: async () => {
      const res = await fetch("/api/admin/analytics");
      if (!res.ok) throw new Error("Metrics loading failed");
      return res.json();
    }
  });

  // Query feature flags
  const { data: flags = [], isLoading: isFlagsLoading } = useQuery({
    queryKey: ["admin-flags"],
    queryFn: async () => {
      const res = await fetch("/api/admin/flags");
      if (!res.ok) throw new Error("Flags loading failed");
      return res.json();
    }
  });

  // Query users list
  const { data: users = [], isLoading: isUsersLoading } = useQuery({
    queryKey: ["admin-users"],
    queryFn: async () => {
      const res = await fetch("/api/admin/users");
      if (!res.ok) throw new Error("Users loading failed");
      return res.json();
    }
  });

  // Toggle flag status mutation
  const toggleFlagMutation = useMutation({
    mutationFn: async ({ name, isActive }: { name: string; isActive: boolean }) => {
      const res = await fetch("/api/admin/flags", {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, isActive })
      });
      if (!res.ok) throw new Error("Flag update failed");
      return res.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin-flags"] });
    }
  });

  // Terminate user account mutation
  const deleteUserMutation = useMutation({
    mutationFn: async (userId: string) => {
      const res = await fetch(`/api/admin/users?userId=${userId}`, { method: "DELETE" });
      if (!res.ok) throw new Error("User deletion failed");
      return res.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin-users"] });
      queryClient.invalidateQueries({ queryKey: ["admin-metrics"] });
    }
  });

  return (
    <div className="space-y-8 max-w-6xl mx-auto">
      {/* Top Banner Heading */}
      <div>
        <h2 className="text-2xl md:text-3xl font-extrabold text-white font-mono uppercase tracking-wider">
          SYSTEM ADMINISTRATOR DECK
        </h2>
        <p className="text-xs text-muted-foreground font-mono">
          Root supervisor node controller. Adjust feature configurations and monitor registries.
        </p>
      </div>

      {/* Aggregated Platform Telemetry Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <CardContent className="p-6 flex items-center justify-between">
            <div className="space-y-1">
              <span className="text-[10px] font-mono text-muted-foreground uppercase tracking-widest block">
                TOTAL CONNECTIONS REGISTERED
              </span>
              {isMetricsLoading ? (
                <Loader2 className="w-5 h-5 animate-spin text-cyber-neon" />
              ) : (
                <span className="text-xl font-bold font-mono text-white block">{metrics.totalUsers || 0} Operators</span>
              )}
            </div>
            <Users className="w-8 h-8 text-cyber-neon opacity-75" />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6 flex items-center justify-between">
            <div className="space-y-1">
              <span className="text-[10px] font-mono text-muted-foreground uppercase tracking-widest block">
                TOTAL PLAYLIST SEQUENCES
              </span>
              {isMetricsLoading ? (
                <Loader2 className="w-5 h-5 animate-spin text-cyber-pink" />
              ) : (
                <span className="text-xl font-bold font-mono text-white block">{metrics.totalPlaylists || 0} wave pools</span>
              )}
            </div>
            <Shield className="w-8 h-8 text-cyber-pink opacity-75" />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6 flex items-center justify-between">
            <div className="space-y-1">
              <span className="text-[10px] font-mono text-muted-foreground uppercase tracking-widest block">
                CORE SYSTEM STATUS
              </span>
              <span className="text-sm font-mono text-green-400 font-bold block flex items-center gap-1.5">
                <CheckCircle2 className="w-4.5 h-4.5 text-green-400 animate-pulse" />
                ONLINE // OPTIMAL
              </span>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Feature Flags management */}
      <div className="space-y-4">
        <h3 className="text-sm font-bold text-cyber-neon tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
          FEATURE CONTROL REGISTERS
        </h3>

        {isFlagsLoading ? (
          <div className="flex justify-center py-6">
            <Loader2 className="w-6 h-6 animate-spin text-cyber-neon" />
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {flags.map((flag: any) => (
              <Card key={flag.id} className={flag.isActive ? "border-cyber-neon/30" : "border-cyber-gray/40"}>
                <CardContent className="p-5 flex items-center justify-between">
                  <div className="space-y-1 truncate max-w-[70%]">
                    <span className="block text-xs font-bold text-white font-mono uppercase truncate">{flag.name}</span>
                    <span className="block text-[10px] text-muted-foreground line-clamp-2 leading-relaxed">{flag.description}</span>
                  </div>
                  <button
                    onClick={() => toggleFlagMutation.mutate({ name: flag.name, isActive: !flag.isActive })}
                    className={`px-3 py-1.5 rounded font-mono text-[10px] uppercase tracking-wider transition-all duration-300 flex items-center gap-1.5 ${
                      flag.isActive 
                        ? "bg-cyber-neon/10 text-cyber-neon border border-cyber-neon/30" 
                        : "bg-cyber-gray/40 text-muted-foreground border border-cyber-gray/30"
                    }`}
                  >
                    <Power className="w-3.5 h-3.5" />
                    <span>{flag.isActive ? "ACTIVE" : "OFFLINE"}</span>
                  </button>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* User registry control table */}
      <div className="space-y-4">
        <h3 className="text-sm font-bold text-cyber-pink tracking-widest font-mono uppercase border-b border-cyber-gray/20 pb-2">
          OPERATOR SECURITY SHELLS
        </h3>

        {isUsersLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-7 h-7 animate-spin text-cyber-pink" />
          </div>
        ) : (
          <div className="bg-cyber-dark/40 rounded-xl border border-cyber-gray/25 p-4 overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>OPERATOR NAME</TableHead>
                  <TableHead>EMAIL SHELL</TableHead>
                  <TableHead className="text-center">PRIVILEGE ROLE</TableHead>
                  <TableHead className="text-center">ACTIONS</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {users.map((user: any) => (
                  <TableRow key={user.id}>
                    <TableCell className="font-bold">{user.name || "Runner Account"}</TableCell>
                    <TableCell className="text-muted-foreground font-mono text-xs">{user.email}</TableCell>
                    <TableCell className="text-center">
                      <span className={`px-2 py-0.5 rounded font-mono text-[9px] uppercase tracking-widest border ${
                        user.role === "ADMIN" 
                          ? "border-cyber-pink/30 text-cyber-pink bg-cyber-pink/5" 
                          : "border-cyber-neon/30 text-cyber-neon bg-cyber-neon/5"
                      }`}>
                        {user.role}
                      </span>
                    </TableCell>
                    <TableCell className="text-center">
                      <button
                        onClick={() => deleteUserMutation.mutate(user.id)}
                        disabled={user.role === "ADMIN"}
                        className="p-1.5 rounded text-muted-foreground hover:text-red-500 disabled:opacity-30 disabled:pointer-events-none transition-colors"
                      >
                        <Trash2 className="w-4.5 h-4.5" />
                      </button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        )}
      </div>
    </div>
  );
}
