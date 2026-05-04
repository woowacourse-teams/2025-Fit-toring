import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "../ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Badge } from "../ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import {
  fetchDummyScenarios,
  fetchDummyStatus,
  insertDummyScenario,
  DummyInsertResponse,
  DummyStatus,
} from "@/services/dummyApi";
import { Database, Loader2, RefreshCw, Search, Upload } from "lucide-react";

export function DummyDataManagement() {
  const [scenarios, setScenarios] = useState<DummyStatus[]>([]);
  const [startAt, setStartAt] = useState("");
  const [insertResult, setInsertResult] = useState<DummyInsertResponse | null>(null);
  const [isLoadingList, setIsLoadingList] = useState(false);
  const [checkingFileSeq, setCheckingFileSeq] = useState<number | null>(null);
  const [insertingFileSeq, setInsertingFileSeq] = useState<number | null>(null);

  const isValidStartAt = startAt.trim().length > 0;
  const isBusy = isLoadingList || checkingFileSeq !== null || insertingFileSeq !== null;

  const toKstOffsetDateTime = (dateTimeLocal: string) => {
    const normalized = dateTimeLocal.length === 16 ? `${dateTimeLocal}:00` : dateTimeLocal;
    return `${normalized}+09:00`;
  };

  const updateScenario = (updated: DummyStatus) => {
    setScenarios((prev) =>
      prev.map((scenario) =>
        scenario.fileSeq === updated.fileSeq ? updated : scenario
      )
    );
  };

  const loadScenarios = async (showToast = false) => {
    try {
      setIsLoadingList(true);
      const response = await fetchDummyScenarios();
      setScenarios(response);
      if (showToast) {
        toast.success("시나리오 파일 목록을 새로고침했습니다.");
      }
    } catch (err) {
      console.error(err);
      toast.error("시나리오 파일 목록 조회에 실패했습니다.");
    } finally {
      setIsLoadingList(false);
    }
  };

  useEffect(() => {
    void loadScenarios();
  }, []);

  const handleStatusCheck = async (fileSeq: number) => {
    try {
      setCheckingFileSeq(fileSeq);
      setInsertResult(null);
      const response = await fetchDummyStatus(fileSeq);
      updateScenario(response);
      toast.success(`${response.scenarioFile} 상태를 조회했습니다.`);
    } catch (err) {
      console.error(err);
      toast.error(`${fileSeq}번 시나리오 상태 조회에 실패했습니다.`);
    } finally {
      setCheckingFileSeq(null);
    }
  };

  const handleInsert = async (scenario: DummyStatus) => {
    if (!isValidStartAt) {
      toast.error("시작 시각을 입력해주세요.");
      return;
    }

    try {
      setInsertingFileSeq(scenario.fileSeq);
      const response = await insertDummyScenario(scenario.fileSeq, toKstOffsetDateTime(startAt));
      setInsertResult(response);
      updateScenario({
        fileSeq: response.fileSeq,
        scenarioFile: response.scenarioFile,
        inserted: true,
      });
      toast.success(`${response.scenarioFile}이 성공적으로 적재되었습니다.`);
    } catch (err) {
      console.error(err);
      toast.error(`${scenario.fileSeq}번 시나리오 적재에 실패했습니다.`);
    } finally {
      setInsertingFileSeq(null);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold">더미 데이터 적재 관리</h2>
        <p className="text-muted-foreground">
          준비된 시나리오 파일의 적재 상태를 확인하고 pending 테이블 적재를 실행합니다.
        </p>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div>
              <CardTitle className="flex items-center gap-2">
                <Database className="h-5 w-5" />
                시나리오 파일 목록
              </CardTitle>
              <CardDescription>
                서버의 dummy 리소스에 포함된 scenarios*.yml 파일 기준입니다.
              </CardDescription>
            </div>
            <Button
              type="button"
              variant="outline"
              onClick={() => loadScenarios(true)}
              disabled={isBusy}
            >
              {isLoadingList ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <RefreshCw className="h-4 w-4" />
              )}
              새로고침
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="grid max-w-sm gap-2">
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
            <p className="text-xs text-muted-foreground">
              적재 시 파일 내 가장 이른 scheduled_at이 이 시각으로 이동됩니다.
            </p>
          </div>

          <div className="rounded-md border">
            {isLoadingList ? (
              <div className="flex h-32 items-center justify-center text-sm text-muted-foreground">
                시나리오 파일 목록을 불러오는 중입니다.
              </div>
            ) : scenarios.length === 0 ? (
              <div className="flex h-32 items-center justify-center text-sm text-muted-foreground">
                준비된 시나리오 파일이 없습니다.
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-24">번호</TableHead>
                    <TableHead>파일명</TableHead>
                    <TableHead className="w-28">상태</TableHead>
                    <TableHead className="w-48 text-right">관리</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {scenarios.map((scenario) => (
                    <TableRow key={scenario.fileSeq}>
                      <TableCell className="font-medium">{scenario.fileSeq}</TableCell>
                      <TableCell>{scenario.scenarioFile}</TableCell>
                      <TableCell>
                        <Badge variant={scenario.inserted ? "default" : "secondary"}>
                          {scenario.inserted ? "적재됨" : "미적재"}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        <div className="flex justify-end gap-2">
                          <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={() => handleStatusCheck(scenario.fileSeq)}
                            disabled={isBusy}
                          >
                            {checkingFileSeq === scenario.fileSeq ? (
                              <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                              <Search className="h-4 w-4" />
                            )}
                            상태 조회
                          </Button>
                          <Button
                            type="button"
                            size="sm"
                            onClick={() => handleInsert(scenario)}
                            disabled={isBusy || scenario.inserted}
                          >
                            {insertingFileSeq === scenario.fileSeq ? (
                              <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                              <Upload className="h-4 w-4" />
                            )}
                            적재
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </div>

          {insertResult && (
            <div className="rounded-lg border bg-card p-4">
              <p className="mb-3 text-sm text-muted-foreground">
                {insertResult.scenarioFile} 적용 시작 시각: {toKstOffsetDateTime(startAt)}
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
