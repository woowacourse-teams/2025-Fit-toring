import { useState } from "react";
import { toast } from "sonner";
import { Button } from "../ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Badge } from "../ui/badge";
import { fetchDummyStatus, insertDummyScenario, DummyInsertResponse, DummyStatus } from "@/services/dummyApi";
import { Database, Loader2, Search, Upload } from "lucide-react";

export function DummyDataManagement() {
  const [fileSeq, setFileSeq] = useState("1");
  const [startAt, setStartAt] = useState("");
  const [status, setStatus] = useState<DummyStatus | null>(null);
  const [insertResult, setInsertResult] = useState<DummyInsertResponse | null>(null);
  const [isChecking, setIsChecking] = useState(false);
  const [isInserting, setIsInserting] = useState(false);

  const parsedFileSeq = Number(fileSeq);
  const isValidFileSeq = Number.isInteger(parsedFileSeq) && parsedFileSeq > 0;
  const isValidStartAt = startAt.trim().length > 0;

  const toKstOffsetDateTime = (dateTimeLocal: string) => {
    const normalized = dateTimeLocal.length === 16 ? `${dateTimeLocal}:00` : dateTimeLocal;
    return `${normalized}+09:00`;
  };

  const handleStatusCheck = async () => {
    if (!isValidFileSeq) {
      toast.error("1 이상의 파일 번호를 입력해주세요.");
      return;
    }

    try {
      setIsChecking(true);
      setInsertResult(null);
      const response = await fetchDummyStatus(parsedFileSeq);
      setStatus(response);
      toast.success(`${response.scenarioFile} 상태를 조회했습니다.`);
    } catch (err) {
      console.error(err);
      setStatus(null);
      toast.error(`${parsedFileSeq}번 시나리오 상태 조회에 실패했습니다.`);
    } finally {
      setIsChecking(false);
    }
  };

  const handleInsert = async () => {
    if (!isValidFileSeq) {
      toast.error("1 이상의 파일 번호를 입력해주세요.");
      return;
    }
    if (!isValidStartAt) {
      toast.error("시작 시각을 입력해주세요.");
      return;
    }

    try {
      setIsInserting(true);
      const response = await insertDummyScenario(parsedFileSeq, toKstOffsetDateTime(startAt));
      setInsertResult(response);
      setStatus({
        fileSeq: response.fileSeq,
        scenarioFile: response.scenarioFile,
        inserted: true,
      });
      toast.success(`${response.scenarioFile}이 성공적으로 적재되었습니다.`);
    } catch (err) {
      console.error(err);
      toast.error(`${parsedFileSeq}번 시나리오 적재에 실패했습니다.`);
    } finally {
      setIsInserting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold">더미 데이터 적재 관리</h2>
        <p className="text-muted-foreground">
          시나리오 파일 번호를 입력해 pending 테이블 적재 상태를 확인하거나 적재를 실행합니다.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Database className="h-5 w-5" />
            시나리오 파일
          </CardTitle>
          <CardDescription>
            파일 번호 1은 scenarios1.yml, 2는 scenarios2.yml에 매핑됩니다.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="grid gap-4 max-w-2xl sm:grid-cols-2">
            <div className="grid gap-2">
              <Label htmlFor="dummy-file-seq">파일 번호</Label>
              <Input
                id="dummy-file-seq"
                type="number"
                min={1}
                step={1}
                value={fileSeq}
                onChange={(event) => {
                  setFileSeq(event.target.value);
                  setStatus(null);
                  setInsertResult(null);
                }}
                placeholder="예: 1"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="dummy-start-at">시작 시각(KST)</Label>
              <Input
                id="dummy-start-at"
                type="datetime-local"
                step={60}
                value={startAt}
                onChange={(event) => {
                  setStartAt(event.target.value);
                  setInsertResult(null);
                }}
              />
            </div>
          </div>

          <div className="flex flex-wrap gap-2">
            <Button
              type="button"
              variant="outline"
              onClick={handleStatusCheck}
              disabled={isChecking || isInserting}
            >
              {isChecking ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Search className="h-4 w-4" />
              )}
              상태 조회
            </Button>
            <Button
              type="button"
              onClick={handleInsert}
              disabled={isChecking || isInserting}
            >
              {isInserting ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Upload className="h-4 w-4" />
              )}
              적재 실행
            </Button>
          </div>

          {status && (
            <div className="rounded-lg border bg-muted/30 p-4 space-y-3">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <p className="font-medium">{status.scenarioFile}</p>
                  <p className="text-sm text-muted-foreground">파일 번호 {status.fileSeq}</p>
                </div>
                <Badge variant={status.inserted ? "default" : "secondary"}>
                  {status.inserted ? "적재됨" : "미적재"}
                </Badge>
              </div>
            </div>
          )}

          {insertResult && (
            <div className="rounded-lg border bg-card p-4">
              <p className="mb-3 text-sm text-muted-foreground">
                적용 시작 시각: {toKstOffsetDateTime(startAt)}
              </p>
              <div className="grid gap-3 sm:grid-cols-3">
                <div>
                  <p className="text-sm text-muted-foreground">시나리오</p>
                  <p className="font-semibold">{insertResult.insertedScenarioCount}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">게시글 pending</p>
                  <p className="font-semibold">{insertResult.insertedPostPendingCount}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">댓글 pending</p>
                  <p className="font-semibold">{insertResult.insertedCommentPendingCount}</p>
                </div>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
