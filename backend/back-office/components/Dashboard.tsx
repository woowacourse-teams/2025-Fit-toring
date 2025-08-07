import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
  SidebarProvider,
  SidebarInset,
  SidebarTrigger,
} from "./ui/sidebar";
import { Button } from "./ui/button";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "./ui/collapsible";
import {
  Users,
  LogOut,
  Menu,
  ChevronRight,
  Award,
  UserCheck,
  BookOpen,
} from "lucide-react";
import { CertificationManagement } from "./dashboard/CertificationManagement";
import { ComingSoon } from "./dashboard/ComingSoon";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { useAuth } from "./AuthContext";

export function Dashboard() {
  const [activeMenu, setActiveMenu] = useState("certifications");
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const renderContent = () => {
    switch (activeMenu) {
      case "certifications":
        return <CertificationManagement />;
      case "mentees":
        return <ComingSoon />;
      case "mentoring":
        return <ComingSoon />;
      default:
        return <CertificationManagement />;
    }
  };

  const getPageTitle = () => {
    switch (activeMenu) {
      case "certifications":
        return "자격증 관리";
      case "mentees":
        return "멘티 관리";
      case "mentoring":
        return "멘토링 관리";
      default:
        return "자격증 관리";
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <SidebarProvider defaultOpen={true}>
        <div className="flex min-h-screen w-full">
          <Sidebar collapsible="icon">
            <SidebarHeader>
              <div className="flex items-center gap-2 px-4 py-2">
                <ImageWithFallback
                  src="https://images.unsplash.com/photo-1560472354-c58ff3013449?w=100&h=100&fit=crop&crop=center"
                  alt="Logo"
                  className="h-10 w-10 object-contain rounded-lg"
                />
                <div className="group-data-[collapsible=icon]:hidden">
                  <h2 className="font-semibold text-[20px]">
                    Fittoring
                  </h2>
                  <p className="text-xs text-muted-foreground text-[13px]">
                    관리자 시스템
                  </p>
                </div>
              </div>
            </SidebarHeader>

            <SidebarContent>
              <SidebarGroup>
                <SidebarGroupLabel>메뉴</SidebarGroupLabel>
                <SidebarGroupContent>
                  <SidebarMenu>
                    <Collapsible
                      defaultOpen
                      className="group/collapsible"
                    >
                      <SidebarMenuItem>
                        <CollapsibleTrigger asChild>
                          <SidebarMenuButton tooltip="멘토 관리">
                            <Users className="h-4 w-4" />
                            <span>멘토 관리</span>
                            <ChevronRight className="ml-auto h-4 w-4 transition-transform group-data-[state=open]/collapsible:rotate-90" />
                          </SidebarMenuButton>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                          <SidebarMenuSub>
                            <SidebarMenuSubItem>
                              <SidebarMenuSubButton
                                asChild
                                isActive={
                                  activeMenu === "certifications"
                                }
                                onClick={() =>
                                  setActiveMenu("certifications")
                                }
                              >
                                <div className="flex items-center cursor-pointer">
                                  <Award className="h-4 w-4" />
                                  <span>자격증 관리</span>
                                </div>
                              </SidebarMenuSubButton>
                            </SidebarMenuSubItem>
                          </SidebarMenuSub>
                        </CollapsibleContent>
                      </SidebarMenuItem>
                    </Collapsible>

                    {/* 멘티 관리 */}
                    <SidebarMenuItem>
                      <SidebarMenuButton
                        tooltip="멘티 관리"
                        isActive={activeMenu === "mentees"}
                        onClick={() => setActiveMenu("mentees")}
                      >
                        <UserCheck className="h-4 w-4" />
                        <span>멘티 관리</span>
                      </SidebarMenuButton>
                    </SidebarMenuItem>

                    {/* 멘토링 관리 */}
                    <SidebarMenuItem>
                      <SidebarMenuButton
                        tooltip="멘토링 관리"
                        isActive={activeMenu === "mentoring"}
                        onClick={() => setActiveMenu("mentoring")}
                      >
                        <BookOpen className="h-4 w-4" />
                        <span>멘토링 관리</span>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  </SidebarMenu>
                </SidebarGroupContent>
              </SidebarGroup>
            </SidebarContent>

            <SidebarFooter>
              <SidebarMenu>
                <SidebarMenuItem>
                  <SidebarMenuButton
                    onClick={handleLogout}
                    tooltip="로그아웃"
                  >
                    <LogOut className="h-4 w-4" />
                    <span>로그아웃</span>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              </SidebarMenu>
            </SidebarFooter>
          </Sidebar>

          <SidebarInset>
            <header className="flex h-16 shrink-0 items-center gap-2 border-b px-6">
              <SidebarTrigger className="-ml-1" />
              <div className="flex items-center gap-2">
                <Menu className="h-5 w-5" />
                <h1 className="font-semibold">
                  {getPageTitle()}
                </h1>
              </div>
            </header>
            
            <main className="flex-1 overflow-auto p-6">
              {renderContent()}
            </main>
          </SidebarInset>
        </div>
      </SidebarProvider>
    </div>
  );
}