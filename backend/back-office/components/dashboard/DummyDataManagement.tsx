import { useState } from "react";
import { toast } from "sonner";
import { Button } from "../ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { insertDummyScenario } from "@/services/dummyApi";
import { Loader2, Database, Upload } from "lucide-react";

export function DummyDataManagement() {
  const [fileSeqs] = useState([1, 2, 3, 4, 5]); // 기본적으로 5개 파일로 가정
  const [isLoading, setIsLoading] = useState<Record<number, boolean>>({});

  const handleInsert = async (fileSeq: number) => {
    try {
      setIsLoading((prev) => ({ ...prev, [fileSeq]: true }));
      await insertDummyScenario(fileSeq);
      toast.success(`${fileSeq}번 시나리오가 성공적으로 적재되었습니다.`);
    } catch (err) {
      console.error(err);
      toast.error(`${fileSeq}번 시나리오 적재에 실패했습니다.`);
    } finally {
      setIsLoading((prev) => ({ ...prev, [fileSeq]: false }));
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold">더미 데이터 적재 관리</h2>
        <p className="text-muted-foreground">시나리오 파일을 선택하여 데이터베이스에 적재합니다.</p>
      </div>

      <div className="border rounded-lg bg-card">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>파일 번호 (Seq)</TableHead>
              <TableHead>파일명</TableHead>
              <TableHead className="text-right">작업</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {fileSeqs.map((seq) => (
              <TableRow key={seq}>
                <TableCell className="font-medium">{seq}</TableCell>
                <TableCell>scenarios{seq}.yml</TableCell>
                <TableCell className="text-right">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => handleInsert(seq)}
                    disabled={isLoading[seq]}
                  >
                    {isLoading[seq] ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      <>
                        <Upload className="h-4 w-4 mr-2" />
                        적재 실행
                      </>
                    )}
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
