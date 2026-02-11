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
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
  PaginationEllipsis,
} from "../ui/pagination";
import { Trash2 } from "lucide-react";
import { fetchDevices, deleteDevice, DeviceItem } from "@/services/deviceApi";

export function DeviceManagement() {
  const [devices, setDevices] = useState<DeviceItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const [currentPage, setCurrentPage] = useState(1); // 1-based
  const pageSize = 20;

  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const loadDevices = async () => {
    setIsLoading(true);
    try {
      const data = await fetchDevices(currentPage, pageSize);
      setDevices(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.total);
    } catch (err) {
      console.error("❌ 기기 목록 조회 실패:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadDevices();
  }, [currentPage]);

  const handlePageChange = (page: number) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("정말로 이 기기를 삭제하시겠습니까?")) return;

    try {
      await deleteDevice(id);
      // 삭제 후 목록 새로고침
      loadDevices();
    } catch (err) {
      console.error("❌ 기기 삭제 실패:", err);
      alert("기기 삭제에 실패했습니다.");
    }
  };

  // Pagination rendering
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
      {/* 헤더 */}
      <div className="flex justify-between items-center">
        <div>
          <h2>기기 관리</h2>
          <p className="text-muted-foreground">등록된 기기를 조회하고 관리할 수 있습니다. (총 {totalElements}개)</p>
        </div>
      </div>

      {/* 테이블 */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="pl-8">ID</TableHead>
              <TableHead>회원명</TableHead>
              <TableHead>회원 ID</TableHead>
              <TableHead>Push Token</TableHead>
              <TableHead className="text-right pr-8">관리</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {isLoading ? (
              <TableRow>
                <TableCell colSpan={5} className="text-center py-10">
                  불러오는 중...
                </TableCell>
              </TableRow>
            ) : devices.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} className="text-center py-10 text-muted-foreground">
                  등록된 기기가 없습니다.
                </TableCell>
              </TableRow>
            ) : (
              devices.map((device) => (
                <TableRow key={device.id}>
                  <TableCell className="font-medium pl-8">{device.id}</TableCell>
                  <TableCell>{device.memberName}</TableCell>
                  <TableCell>{device.memberId}</TableCell>
                  <TableCell className="max-w-xs truncate" title={device.pushToken}>
                    {device.pushToken}
                  </TableCell>
                  <TableCell className="text-right pr-8">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleDelete(device.id)}
                      className="text-red-500 hover:text-red-700 hover:bg-red-50"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {renderPagination()}
    </div>
  );
}
