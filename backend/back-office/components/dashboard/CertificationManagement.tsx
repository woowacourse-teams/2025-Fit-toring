import { useState, useEffect } from "react";
import { Badge } from "../ui/badge";
import { toast } from "sonner";
import { formatDateTime } from "@/utils/Formatter";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "../ui/pagination";
import { Label } from "../ui/label";
import { Button } from "../ui/button";
import { ImageWithFallback } from "../figma/ImageWithFallback";
import {
  CheckCircle,
  Clock,
  XCircle,
  Award,
  GraduationCap,
  Trophy,
  FileText,
  Loader2,
} from "lucide-react";
import {
  fetchCertificates,
  fetchCertificateDetail,
  rejectCertificate,
  approveCertificate,
  CertificateData,
} from "../../services/certificateApi";

export function CertificationManagement() {
  const [statusFilter, setStatusFilter] = useState("all");
  const [selectedCert, setSelectedCert] =
    useState<CertificateData | null>(null);
  const [isDetailDialogOpen, setIsDetailDialogOpen] =
    useState(false);
  const [certifications, setCertifications] = useState<
    CertificateData[]
  >([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // 데이터 로드 - 필터 조건에 따른 데이터 로드
  useEffect(() => {
    const loadCertifications = async () => {
      try {
        console.log('📋 자격증명 목록 로드 시작:', { statusFilter, page: currentPage });
        setIsLoading(true);
        setError(null);
        const data = await fetchCertificates(statusFilter, currentPage - 1, 10);
        setCertifications(data.certificates);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
        console.log('✅ UI 상태 업데이트 완료:', { loadedCount: data.certificates.length });
        
        // 필터 적용 결과 토스트 (초기 로드 제외)
        if (!isInitialLoad && statusFilter !== 'all') {
          const filterName = statusFilter === 'PENDING' ? '검토 중' : 
                           statusFilter === 'REJECTED' ? '반려' : 
                           statusFilter === 'APPROVED' ? '인증 완료' : statusFilter;
          toast.success(`${filterName} 상태 필터가 적용되었습니다. ${data.certificates.length}건의 자격증명이 조회되었습니다.`);
        }
      } catch (err) {
        setError("자격증명 데이터를 불러오는데 실패했습니다.");
        console.error("❌ 자격증명 로드 실패:", err);
      } finally {
        setIsLoading(false);
        if (isInitialLoad) {
          setIsInitialLoad(false);
        }
      }
    };

    loadCertifications();
  }, [statusFilter, currentPage]);

  // 상태 필터 변경 핸들러
  const handleStatusFilterChange = (value: string) => {
    console.log('🔄 필터 변경:', { from: statusFilter, to: value });
    setStatusFilter(value);
    setCurrentPage(1);
  };

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
        setCurrentPage(page);
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "PENDING":
        return (
          <Badge
            variant="outline"
            className="border-amber-300 text-amber-700 bg-amber-50 hover:bg-amber-100 text-[12px] font-medium"
          >
            <Clock className="h-3 w-3 mr-1" />
            검토 중
          </Badge>
        );
      case "REJECTED":
        return (
          <Badge 
            variant="outline"
            className="border-red-300 text-red-700 bg-red-50 hover:bg-red-100 text-[12px] font-medium"
          >
            <XCircle className="h-3 w-3 mr-1" />
            반려
          </Badge>
        );
      case "APPROVED":
        return (
          <Badge
            variant="outline"
            className="border-emerald-300 text-emerald-700 bg-emerald-50 hover:bg-emerald-100 text-[12px] font-medium"
          >
            <CheckCircle className="h-3 w-3 mr-1" />
            인증 완료
          </Badge>
        );
    }
  };

  const getCertTypeBadge = (certType: string) => {
    switch (certType) {
      case "LICENSE":
        return (
          <Badge
            variant="outline"
            className="border-blue-300 text-blue-700 bg-blue-50 hover:bg-blue-100 text-[12px] font-medium"
          >
            <FileText className="h-3 w-3 mr-1" />
            LICENSE
          </Badge>
        );
      case "EDUCATION":
        return (
          <Badge
            variant="outline"
            className="border-purple-300 text-purple-700 bg-purple-50 hover:bg-purple-100 text-[12px] font-medium"
          >
            <GraduationCap className="h-3 w-3 mr-1" />
            EDUCATION
          </Badge>
        );
      case "AWARD":
        return (
          <Badge
            variant="outline"
            className="border-yellow-400 text-yellow-700 bg-yellow-50 hover:bg-yellow-100 text-[12px] font-medium"
          >
            <Trophy className="h-3 w-3 mr-1" />
            AWARD
          </Badge>
        );
      case "ETC":
        return (
          <Badge
            variant="outline"
            className="border-gray-400 text-gray-700 bg-gray-50 hover:bg-gray-100 text-[12px] font-medium"
          >
            <Award className="h-3 w-3 mr-1" />
            ETC
          </Badge>
        );
      default:
        return (
          <Badge 
            variant="secondary"
            className="text-[12px] font-medium"
          >
            <FileText className="h-3 w-3 mr-1" />
            {certType}
          </Badge>
        );
    }
  };

  const handleCertClick = async (cert: CertificateData) => {
    try {
      // 상세 정보를 서버에서 가져옴
      const detailData = await fetchCertificateDetail(cert.id);
      // 목록의 자격증명을 상세 데이터에 추가
      const mergedData = {
        ...detailData,
        certificationName: cert.certificationName,
      };
      setSelectedCert(mergedData);
      setIsDetailDialogOpen(true);
    } catch (err) {
      console.error("자격증명 상세 정보 로드 실패:", err);
      // 실패 시 기본 정보 사용
      setSelectedCert(cert);
      setIsDetailDialogOpen(true);
      toast.error("자격증명 상세 정보를 불러오는데 실패했습니다.");
    }
  };

  const handleReject = async (certId: number) => {
    try {
      // 관리자 권한 및 PENDING 상태 확인
      const cert = certifications.find((c) => c.id === certId);
      if (!cert || cert.status !== "PENDING") {
        toast.error(
          "검토 중 상태인 자격증명만 반려할 수 있습니다.",
        );
        return;
      }

      await rejectCertificate(certId);

      // 로컬 상태 업데이트
      setCertifications((prev) =>
        prev.map((cert) =>
          cert.id === certId
            ? {
                ...cert,
                status: "REJECTED" as const,
                rejectionReason: "관리자에 의해 반려됨",
              }
            : cert,
        ),
      );

      // 선택된 자격증 정보도 업데이트
      if (selectedCert && selectedCert.id === certId) {
        setSelectedCert((prev) =>
          prev
            ? {
                ...prev,
                status: "REJECTED" as const,
                rejectionReason: "관리자에 의해 반료됨",
              }
            : null,
        );
      }

      setIsDetailDialogOpen(false);
      toast.success("자격증명이 반려되었습니다.");
    } catch (err) {
      console.error("Error rejecting certificate:", err);
      toast.error("자격증명 반료 중 오류가 발생했습니다.");
    }
  };

  const handleApprove = async (certId: number) => {
    try {
      // 관리자 권한 및 PENDING 상태 확인
      const cert = certifications.find((c) => c.id === certId);
      if (!cert || cert.status !== "PENDING") {
        toast.error(
          "검토 중 상태인 자격증명만 승인할 수 있습니다.",
        );
        return;
      }

      await approveCertificate(certId);

      // 로컬 상태 업데이트
      setCertifications((prev) =>
        prev.map((cert) =>
          cert.id === certId
            ? { ...cert, status: "APPROVED" as const }
            : cert,
        ),
      );

      // 선택된 자격증 정보도 업데이트
      if (selectedCert && selectedCert.id === certId) {
        setSelectedCert((prev) =>
          prev
            ? { ...prev, status: "APPROVED" as const }
            : null,
        );
      }

      setIsDetailDialogOpen(false);
      toast.success("자격증명이 승인되었습니다.");
    } catch (err) {
      console.error("Error approving certificate:", err);
      toast.error("자격증명 승인 중 오류가 발생했습니다.");
    }
  };

  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const pageNumbers = [];
    const maxPagesToShow = 5;
    let startPage, endPage;

    if (totalPages <= maxPagesToShow) {
        startPage = 1;
        endPage = totalPages;
    } else {
        const maxPagesBeforeCurrent = Math.floor(maxPagesToShow / 2);
        const maxPagesAfterCurrent = Math.ceil(maxPagesToShow / 2) - 1;
        if (currentPage <= maxPagesBeforeCurrent) {
            startPage = 1;
            endPage = maxPagesToShow;
        } else if (currentPage + maxPagesAfterCurrent >= totalPages) {
            startPage = totalPages - maxPagesToShow + 1;
            endPage = totalPages;
        } else {
            startPage = currentPage - maxPagesBeforeCurrent;
            endPage = currentPage + maxPagesAfterCurrent;
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        pageNumbers.push(i);
    }

    return (
        <div className="flex items-center justify-end py-4 px-6">
            <Pagination>
                <PaginationContent>
                    <PaginationItem>
                        <PaginationPrevious
                            href="#"
                            onClick={(e) => { e.preventDefault(); handlePageChange(currentPage - 1); }}
                            className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
                        />
                    </PaginationItem>
                    
                    {startPage > 1 && (
                        <>
                            <PaginationItem>
                                <PaginationLink href="#" onClick={(e) => { e.preventDefault(); handlePageChange(1); }}>1</PaginationLink>
                            </PaginationItem>
                            {startPage > 2 && <PaginationItem><PaginationEllipsis /></PaginationItem>}
                        </>
                    )}

                    {pageNumbers.map(number => (
                        <PaginationItem key={number}>
                            <PaginationLink
                                href="#"
                                onClick={(e) => { e.preventDefault(); handlePageChange(number); }}
                                isActive={currentPage === number}
                            >
                                {number}
                            </PaginationLink>
                        </PaginationItem>
                    ))}

                    {endPage < totalPages && (
                        <>
                            {endPage < totalPages - 1 && <PaginationItem><PaginationEllipsis /></PaginationItem>}
                            <PaginationItem>
                                <PaginationLink href="#" onClick={(e) => { e.preventDefault(); handlePageChange(totalPages); }}>{totalPages}</PaginationLink>
                            </PaginationItem>
                        </>
                    )}

                    <PaginationItem>
                        <PaginationNext
                            href="#"
                            onClick={(e) => { e.preventDefault(); handlePageChange(currentPage + 1); }}
                            className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
                        />
                    </PaginationItem>
                </PaginationContent>
            </Pagination>
        </div>
    );
  }

  return (
    <div className="h-full flex flex-col">
      {/* 헤더 */}
      <div className="mb-6">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
          <div>
            <h1 className="text-2xl font-semibold">
              자격증명 관리
            </h1>
            <p className="text-muted-foreground mt-1">
              멘토들의 자격증명 정보를 관리하고 검증합니다.
              {!isLoading && (
                <span className="ml-2 text-primary font-medium">
                  총 {totalElements}건
                </span>
              )}
            </p>
          </div>
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <Label className="text-sm text-muted-foreground">필터:</Label>
              <Select
                value={statusFilter}
                onValueChange={handleStatusFilterChange}
              >
                <SelectTrigger className="w-40">
                  <SelectValue placeholder="상태" defaultValue="ALL" />
                  
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">모든 상태</SelectItem>
                  <SelectItem value="PENDING">검토 중</SelectItem>
                  <SelectItem value="REJECTED">반려</SelectItem>
                  <SelectItem value="APPROVED">
                    인증 완료
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>
      </div>

      {/* 테이블 컨테이너 - 스크롤 가능 */}
      <div className="flex-1 border rounded-lg bg-card overflow-hidden flex flex-col">
        <div
          className="flex-1 overflow-auto"
          style={{ maxHeight: "calc(100vh - 260px)" }}
        >
          {isLoading ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin mr-2" />
              <p className="text-muted-foreground">
                자격증 데이터를 불러오는 중...
              </p>
            </div>
          ) : error ? (
            <div className="text-center py-12">
              <p className="text-red-600">{error}</p>
              <Button
                size="sm"
                variant="outline"
                className="mt-4"
                onClick={() => window.location.reload()}
              >
                다시 시도
              </Button>
            </div>
          ) : (
            <>
              <Table>
                <TableHeader className="sticky top-0 bg-card z-0">
                  <TableRow>
                    <TableHead className="px-6 py-4">
                      멘토 정보
                    </TableHead>
                    <TableHead className="px-6 py-4">
                      자격증명 정보
                    </TableHead>
                    <TableHead className="px-6 py-4">
                      인증 유형
                    </TableHead>
                    <TableHead className="px-6 py-4">
                      상태
                    </TableHead>
                    <TableHead className="px-6 py-4">
                      제출일
                    </TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {certifications.map((cert) => (
                    <TableRow
                      key={cert.id}
                      className="h-12"
                    >
                      <TableCell className="font-medium px-5 py-3">
                        <div>
                          <p className="font-medium">
                            {cert.mentorName}
                          </p>
                        </div>
                      </TableCell>
                      <TableCell className="px-6 py-3">
                        <div
                          className="cursor-pointer hover:text-primary transition-colors"
                          onClick={() => handleCertClick(cert)}
                        >
                          <p className="font-medium hover:underline">
                            {cert.certificationName}
                          </p>
                        </div>
                      </TableCell>
                      <TableCell className="px-6 py-3">
                        <div className="flex items-center">
                          {getCertTypeBadge(cert.certType)}
                        </div>
                      </TableCell>
                      <TableCell className="px-6 py-3">
                        <div className="flex items-center">
                          {getStatusBadge(cert.status)}
                        </div>
                      </TableCell>
                      <TableCell className="text-sm text-muted-foreground px-6 py-3">
                        {cert.submittedAt}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              {certifications.length === 0 && !isLoading && (
                <div className="text-center py-12">
                  <div className="flex flex-col items-center space-y-3">
                    <FileText className="h-12 w-12 text-muted-foreground/50" />
                    <div>
                      <p className="font-medium text-muted-foreground">
                        {statusFilter === 'all' ? '등록된 자격증명이 없습니다' : '조건에 맞는 자격증명이 없습니다'}
                      </p>
                      <p className="text-sm text-muted-foreground/80 mt-1">
                        {statusFilter !== 'all' && '다른 필터 조건을 시도해보세요'}
                      </p>
                    </div>
                    {statusFilter !== 'all' && (
                      <Button 
                        variant="outline" 
                        size="sm"
                        onClick={() => handleStatusFilterChange('all')}
                      >
                        모든 상태 보기
                      </Button>
                    )}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
        {renderPagination()}
      </div>

      {/* 자격증 상세 다이얼로그 */}
      <Dialog
        open={isDetailDialogOpen}
        onOpenChange={setIsDetailDialogOpen}
      >
        <DialogContent className="sm:max-w-[600px]">
          {selectedCert && (
            <>
              <DialogHeader>
                <DialogTitle className="flex items-center space-x-2">
                  <Award className="h-5 w-5" />
                  <span>자격증명 상세 정보</span>
                </DialogTitle>
                <DialogDescription>
                  자격증명 이미지를 확인하고 승인 또는 반려 처리를 할 수 있습니다.
                </DialogDescription>
              </DialogHeader>

              <div className="space-y-6">
                {/* 자격증명 이미지 */}
                <div className="space-y-2">
                  <Label>자격증명 이미지</Label>
                  <div className="border rounded-lg overflow-hidden">
                    <ImageWithFallback
                      src={selectedCert.imageUrl}
                      alt={selectedCert.certificationName}
                      className="w-full h-64 object-cover"
                    />
                  </div>
                </div>

                {/* 기본 정보 */}
                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label>자격증명</Label>
                    <p className="font-medium">
                      {selectedCert.certificationName}
                    </p>
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label>멘토 정보</Label>
                      <div>
                        <p className="font-medium">
                          {selectedCert.mentorName}
                        </p>
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label>인증 유형</Label>
                      <div className="flex items-center">
                        {getCertTypeBadge(selectedCert.certType)}
                      </div>
                    </div>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>제출일</Label>
                    <p className="text-sm text-[15px]">
                    {formatDateTime(selectedCert.submittedAt)}
                    </p>
                  </div>

                  <div className="space-y-2">
                    <Label>현재 상태</Label>
                    <div className="flex items-center">
                      {getStatusBadge(selectedCert.status)}
                    </div>
                  </div>
                </div>
              </div>

              <DialogFooter className="space-x-2">
                {selectedCert.status === "PENDING" && (
                  <>
                    <Button
                      size="sm"
                      variant="outline"
                      className="text-red-600 border-red-300 hover:bg-red-50"
                      onClick={() =>
                        handleReject(selectedCert.id)
                      }
                    >
                      <XCircle className="h-4 w-4 mr-2" />
                      반려
                    </Button>
                    <Button
                      size="sm"
                      className="bg-green-600 hover:bg-green-700"
                      onClick={() =>
                        handleApprove(selectedCert.id)
                      }
                    >
                      <CheckCircle className="h-4 w-4 mr-2" />
                      승인
                    </Button>
                  </>
                )}
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => setIsDetailDialogOpen(false)}
                >
                  닫기
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}