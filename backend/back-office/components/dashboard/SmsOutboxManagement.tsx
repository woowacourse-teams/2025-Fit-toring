import { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { Button } from "../ui/button";
import { Badge } from "../ui/badge";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import {
  Tabs,
  TabsList,
  TabsTrigger,
} from "../ui/tabs";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
  PaginationEllipsis,
} from "../ui/pagination";
import { RotateCcw, Eye } from "lucide-react";
import {
  fetchSmsOutboxList,
  fetchSmsOutboxDetail,
  retrySmsOutbox,
  SmsOutboxItem,
  SmsOutboxDetail,
  SmsOutboxStatus,
} from "@/services/smsOutboxApi";

const STATUS_TABS: { value: SmsOutboxStatus; label: string }[] = [
  { value: "FAILED", label: "FAILED" },
  { value: "PENDING", label: "PENDING" },
  { value: "PROCESSING", label: "PROCESSING" },
  { value: "SENT", label: "SENT" },
];

const STATUS_VARIANTS: Record<SmsOutboxStatus, "default" | "secondary" | "destructive" | "outline"> = {
  FAILED: "destructive",
  PROCESSING: "secondary",
  PENDING: "outline",
  SENT: "default",
};

export function SmsOutboxManagement() {
  const [status, setStatus] = useState<SmsOutboxStatus>("FAILED");
  const [items, setItems] = useState<SmsOutboxItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 20;

  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const [selectedDetail, setSelectedDetail] = useState<SmsOutboxDetail | null>(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isRetrying, setIsRetrying] = useState(false);

  const loadList = async () => {
    setIsLoading(true);
    try {
      const data = await fetchSmsOutboxList(status, currentPage, pageSize);
      setItems(data.content);
      setTotalPages(Math.max(1, data.totalPages));
      setTotalElements(data.total);
    } catch (err) {
      console.error("❌ SMS Outbox 목록 조회 실패:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status, currentPage]);

  const handleStatusChange = (next: string) => {
    const nextStatus = next as SmsOutboxStatus;
    if (nextStatus !== status) {
      setStatus(nextStatus);
      setCurrentPage(1);
    }
  };

  const handlePageChange = (page: number) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  const handleOpenDetail = async (id: number) => {
    try {
      const detail = await fetchSmsOutboxDetail(id);
      setSelectedDetail(detail);
      setIsDetailOpen(true);
    } catch (err) {
      console.error("❌ SMS Outbox 상세 조회 실패:", err);
      alert("상세 정보를 불러오지 못했습니다.");
    }
  };

  const handleRetry = async (id: number) => {
    if (!window.confirm("이 row를 다시 발송 큐에 올리시겠습니까? (다음 publisher tick에서 발송됩니다)")) {
      return;
    }
    setIsRetrying(true);
    try {
      await retrySmsOutbox(id);
      setIsDetailOpen(false);
      setSelectedDetail(null);
      await loadList();
    } catch (err) {
      console.error("❌ SMS Outbox 재시도 실패:", err);
      alert(err instanceof Error ? err.message : "재시도에 실패했습니다.");
    } finally {
      setIsRetrying(false);
    }
  };

  const formatDateTime = (value: string | null) => {
    if (!value) return "-";
    return value.replace("T", " ").split(".")[0];
  };

  const truncate = (text: string | null, limit = 60) => {
    if (!text) return "-";
    return text.length <= limit ? text : `${text.slice(0, limit)}...`;
  };

  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const pagesToShow = 5;
    const start = Math.max(1, currentPage - 2);
    const end = Math.min(totalPages, start + pagesToShow - 1);

    return (
      <div className="flex justify-end py-4 px-6">
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  handlePageChange(currentPage - 1);
                }}
                className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
              />
            </PaginationItem>

            {start > 1 && (
              <>
                <PaginationItem>
                  <PaginationLink href="#" onClick={() => handlePageChange(1)}>1</PaginationLink>
                </PaginationItem>
                {start > 2 && <PaginationEllipsis />}
              </>
            )}

            {Array.from({ length: end - start + 1 }, (_, i) => start + i).map((num) => (
              <PaginationItem key={num}>
                <PaginationLink
                  href="#"
                  onClick={(e) => {
                    e.preventDefault();
                    handlePageChange(num);
                  }}
                  isActive={currentPage === num}
                >
                  {num}
                </PaginationLink>
              </PaginationItem>
            ))}

            {end < totalPages && (
              <>
                {end < totalPages - 1 && <PaginationEllipsis />}
                <PaginationItem>
                  <PaginationLink href="#" onClick={() => handlePageChange(totalPages)}>
                    {totalPages}
                  </PaginationLink>
                </PaginationItem>
              </>
            )}

            <PaginationItem>
              <PaginationNext
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  handlePageChange(currentPage + 1);
                }}
                className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </div>
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2>SMS Outbox</h2>
          <p className="text-muted-foreground">
            SMS 발송 큐 상태를 확인하고 FAILED row를 수동 재시도할 수 있습니다. (총 {totalElements}개)
          </p>
        </div>
      </div>

      <Tabs value={status} onValueChange={handleStatusChange}>
        <TabsList>
          {STATUS_TABS.map((tab) => (
            <TabsTrigger key={tab.value} value={tab.value}>
              {tab.label}
            </TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="pl-8">ID</TableHead>
              <TableHead>예약 ID</TableHead>
              <TableHead>이벤트</TableHead>
              <TableHead>수신번호</TableHead>
              <TableHead>상태</TableHead>
              <TableHead>시도</TableHead>
              <TableHead>마지막 에러</TableHead>
              <TableHead>업데이트</TableHead>
              <TableHead className="text-right pr-8">관리</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {isLoading ? (
              <TableRow>
                <TableCell colSpan={9} className="text-center py-10">
                  불러오는 중...
                </TableCell>
              </TableRow>
            ) : items.length === 0 ? (
              <TableRow>
                <TableCell colSpan={9} className="text-center py-10 text-muted-foreground">
                  표시할 row가 없습니다.
                </TableCell>
              </TableRow>
            ) : (
              items.map((row) => (
                <TableRow key={row.id}>
                  <TableCell className="font-medium pl-8">{row.id}</TableCell>
                  <TableCell>{row.reservationId}</TableCell>
                  <TableCell>{row.eventType}</TableCell>
                  <TableCell>{row.toPhone}</TableCell>
                  <TableCell>
                    <Badge variant={STATUS_VARIANTS[row.status]}>{row.status}</Badge>
                  </TableCell>
                  <TableCell>{row.attempts}</TableCell>
                  <TableCell className="max-w-xs truncate" title={row.lastError ?? undefined}>
                    {truncate(row.lastError)}
                  </TableCell>
                  <TableCell className="text-xs text-muted-foreground">
                    {formatDateTime(row.updatedAt)}
                  </TableCell>
                  <TableCell className="text-right pr-8">
                    <div className="flex justify-end gap-1">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleOpenDetail(row.id)}
                        title="상세 보기"
                      >
                        <Eye className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleRetry(row.id)}
                        disabled={row.status !== "FAILED" || isRetrying}
                        title={row.status === "FAILED" ? "재시도" : "FAILED 상태만 재시도 가능"}
                      >
                        <RotateCcw className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {renderPagination()}

      <Dialog open={isDetailOpen} onOpenChange={setIsDetailOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>SMS Outbox 상세</DialogTitle>
            <DialogDescription>
              {selectedDetail ? `outboxId=${selectedDetail.id}, reservationId=${selectedDetail.reservationId}` : ""}
            </DialogDescription>
          </DialogHeader>

          {selectedDetail && (
            <div className="space-y-4 text-sm">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <p className="text-muted-foreground">상태</p>
                  <Badge variant={STATUS_VARIANTS[selectedDetail.status]}>{selectedDetail.status}</Badge>
                </div>
                <div>
                  <p className="text-muted-foreground">이벤트</p>
                  <p>{selectedDetail.eventType}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">수신번호</p>
                  <p>{selectedDetail.toPhone}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">시도 횟수</p>
                  <p>{selectedDetail.attempts}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">생성</p>
                  <p>{formatDateTime(selectedDetail.createdAt)}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">업데이트</p>
                  <p>{formatDateTime(selectedDetail.updatedAt)}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">PROCESSING 시작</p>
                  <p>{formatDateTime(selectedDetail.processingStartedAt)}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">FAILED 알림</p>
                  <p>{formatDateTime(selectedDetail.failedNotifiedAt)}</p>
                </div>
              </div>

              <div>
                <p className="text-muted-foreground mb-1">제목</p>
                <p className="rounded-md border bg-muted/30 p-3">{selectedDetail.subject}</p>
              </div>
              <div>
                <p className="text-muted-foreground mb-1">메시지 전문</p>
                <pre className="whitespace-pre-wrap rounded-md border bg-muted/30 p-3 text-xs">
                  {selectedDetail.message}
                </pre>
              </div>
              {selectedDetail.lastError && (
                <div>
                  <p className="text-muted-foreground mb-1">마지막 에러</p>
                  <pre className="whitespace-pre-wrap rounded-md border bg-red-50 text-red-900 p-3 text-xs">
                    {selectedDetail.lastError}
                  </pre>
                </div>
              )}
            </div>
          )}

          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDetailOpen(false)}>
              닫기
            </Button>
            {selectedDetail && (
              <Button
                onClick={() => handleRetry(selectedDetail.id)}
                disabled={selectedDetail.status !== "FAILED" || isRetrying}
              >
                <RotateCcw className="h-4 w-4 mr-2" />
                {isRetrying ? "재시도 중..." : "재시도"}
              </Button>
            )}
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
