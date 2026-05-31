import { Sidebar } from "@/components/layout/Sidebar";
import { Header } from "@/components/layout/Header";
import { MobileNav } from "@/components/layout/MobileNav";

export default function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen bg-cyber-black">
      {/* Platform Navigation Sidebar */}
      <Sidebar />

      {/* Main workspace container */}
      <div className="flex-1 flex flex-col min-w-0">
        <Header />
        
        {/* Scrollable contents frame */}
        <div className="flex-grow p-6 md:p-8 overflow-y-auto pb-24 md:pb-8 relative z-10">
          {children}
        </div>
      </div>

      {/* Handheld layout triggers */}
      <MobileNav />
    </div>
  );
}
