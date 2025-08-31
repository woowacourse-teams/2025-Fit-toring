import { useState, useEffect } from "react";
import { useNavigate, useLocation, useParams } from "react-router-dom";
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
  Settings,
  Tag,
  Users,
  LogOut,
  Menu,
  ChevronRight,
  Award,
  UserCheck,
  BookOpen,
} from "lucide-react";
import { CertificationManagement } from "./dashboard/CertificationManagement";
import { MentoringManagement } from "./dashboard/MentoringManagement";
import { MentoringDetail } from "./dashboard/MentoringDetail";
import { ComingSoon } from "./dashboard/ComingSoon";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { useAuth } from "./AuthContext";
import { logout as apiLogout } from "../services/authApi";
import { ROUTES } from "../constants/routes";
import { MenteeManagement } from "./dashboard/MenteeManagement";

export function Dashboard() {
  const [activeMenu, setActiveMenu] = useState("certifications");
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const params = useParams();

  // URL 경로에 따라 activeMenu 설정
  useEffect(() => {
    if (location.pathname.startsWith('/mentoring/')) {
      setActiveMenu('mentoring-detail');
    } else if (location.pathname === '/' || location.pathname === '/dashboard') {
      // 해시가 있으면 해당 메뉴로 이동
      if (location.hash === '#mentoring') setActiveMenu('mentoring');
      else if (location.hash === '#category') setActiveMenu('category');
      else setActiveMenu('certifications');
    }
  }, [location.pathname, location.hash]);

  const handleMenuClick = (menu: 'certifications'|'mentees'|'mentoring'|'category') => {
        switch (menu) {
          case 'certifications':
            setActiveMenu('certifications');
            navigate(ROUTES.ROOT);
            break;
          case 'mentees':
            setActiveMenu('mentees');
            navigate(ROUTES.ROOT);
            break;
          case 'mentoring':
            setActiveMenu('mentoring');
            navigate(`${ROUTES.ROOT}#mentoring`);
            break;
          case 'category':
            setActiveMenu('category');
            navigate(`${ROUTES.ROOT}#category`);
            break;
        }
      };

  const handleLogout = async () => {
    setIsLoggingOut(true);
    
    try {
      console.log('🚪 Attempting logout...');
      // 서버에 로그아웃 요청
      await apiLogout();
      console.log('✅ Server logout successful');
    } catch (error) {
      console.error('❌ Server logout failed:', error);
      // 서버 로그아웃 실패해도 클라이언트 로그아웃은 진행
    } finally {
      // 클라이언트 로그아웃 (상태 초기화)
      logout();
      navigate(ROUTES.LOGIN);
      setIsLoggingOut(false);
    }
  };

  const renderContent = () => {
    switch (activeMenu) {
      case "certifications":
        return <CertificationManagement />;
      case "mentees":
        return <MenteeManagement />;
      case "mentoring":
        return <MentoringManagement />;
      case "mentoring-detail":
        return <MentoringDetail />;
      case "category":
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
      case "mentoring-detail":
        return "멘토링 상세";
      case "category":
        return "카테고리 관리";
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
                  src="/web-admin/fittoring.png"
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
                    <Collapsible
                    defaultOpen
                    className="group/collapsible"
                    >
                      <SidebarMenuItem>
                        <CollapsibleTrigger asChild>
                          <SidebarMenuButton tooltip="멘토링 관리">
                            <BookOpen className="h-4 w-4" />
                            <span>멘토링 관리</span>
                            <ChevronRight className="ml-auto h-4 w-4 transition-transform group-data-[state=open]/collapsible:rotate-90" />
                          </SidebarMenuButton>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                          <SidebarMenuSub>
                            <SidebarMenuSubItem>
                              <SidebarMenuSubButton
                                asChild
                                isActive={activeMenu === "mentoring" || activeMenu === "mentoring-detail"}
                                onClick={() => handleMenuClick("mentoring")}
                              >
                                <div className="flex items-center cursor-pointer">
                                  <Settings className="h-4 w-4" />
                                  <span>멘토링 상세 관리</span>
                                </div>
                              </SidebarMenuSubButton>
                            </SidebarMenuSubItem>
                            <SidebarMenuSubItem>
                              <SidebarMenuSubButton
                                asChild
                                isActive={activeMenu === "category"}
                                onClick={() => handleMenuClick("category")}
                              >
                                <div className="flex items-center cursor-pointer">
                                  <Tag className="h-4 w-4" />
                                  <span>카테고리 관리</span>
                                </div>
                              </SidebarMenuSubButton>
                            </SidebarMenuSubItem>
                          </SidebarMenuSub>
                        </CollapsibleContent>
                      </SidebarMenuItem>
                    </Collapsible>
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
                    disabled={isLoggingOut}
                  >
                    <LogOut className="h-4 w-4" />
                    <span>{isLoggingOut ? "로그아웃 중..." : "로그아웃"}</span>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              </SidebarMenu>
              
              {/* 현재 사용자 정보 표시 */}
              {user && (
                <div className="px-4 py-2 border-t border-sidebar-border group-data-[collapsible=icon]:hidden">
                  <p className="text-xs text-muted-foreground">로그인:</p>
                  <p className="text-sm font-medium truncate">{user.name}</p>
                  <p className="text-xs text-muted-foreground truncate">@{user.loginId}</p>
                  <p className="text-xs text-muted-foreground truncate">{user.role}</p>
                </div>
              )}
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
