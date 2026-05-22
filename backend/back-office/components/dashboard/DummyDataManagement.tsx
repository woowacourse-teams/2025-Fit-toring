import { ChangeEvent, useEffect, useRef, useState } from "react";
import { toast } from "sonner";
import { Button } from "../ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card";
import { Input } from "../ui/input";
import { Badge } from "../ui/badge";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import {
  DummyCommentPreview,
  DummyPostPreview,
  DummyScenarioPreview,
  fetchDummyScenarios,
  fetchDummyPreview,
  fetchDummyStatus,
  insertDummyScenario,
  uploadDummyScenario,
  DummyStatus,
} from "@/services/dummyApi";
import {
  hhmmToIsoDuration,
  isoDurationToHHMM,
  isoDurationToMilliseconds,
  minutesToHHMM,
} from "@/services/dummyDuration";
import {
  Clock,
  Database,
  Eye,
  FileUp,
  Loader2,
  MessageSquare,
  RefreshCw,
  Search,
  Upload,
} from "lucide-react";

export function DummyDataManagement() {
  const [scenarios, setScenarios] = useState<DummyStatus[]>([]);
  const [startAtMap, setStartAtMap] = useState<Record<number, string>>({});
  const [durationMap, setDurationMap] = useState<Record<number, string>>({});
  const [isLoadingList, setIsLoadingList] = useState(false);
  const [checkingFileSeq, setCheckingFileSeq] = useState<number | null>(null);
  const [insertingFileSeq, setInsertingFileSeq] = useState<number | null>(null);
  const [previewingFileSeq, setPreviewingFileSeq] = useState<number | null>(null);
  const [previewScenario, setPreviewScenario] = useState<DummyStatus | null>(null);
  const [preview, setPreview] = useState<DummyScenarioPreview | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const isBusy =
    isLoadingList || checkingFileSeq !== null || insertingFileSeq !== null || isUploading;

  const toKstOffsetDateTime = (dateTimeLocal: string) => {
    const normalized = dateTimeLocal.length === 16 ? `${dateTimeLocal}:00` : dateTimeLocal;
    return `${normalized}+09:00`;
  };

  const formatAppliedStartAt = (iso: string) => {
    return formatKstDateTime(Date.parse(iso));
  };

  const safeIsoToHHMM = (iso: string) => {
    try {
      return isoDurationToHHMM(iso);
    } catch {
      return "00:00";
    }
  };

  const safeIsoToMs = (iso: string) => {
    try {
      return isoDurationToMilliseconds(iso);
    } catch {
      return 0;
    }
  };

  const isOriginalDurationZero = (iso: string) => safeIsoToMs(iso) === 0;

  const isInsertDisabled = (scenario: DummyStatus) => {
    if (isBusy || scenario.inserted) {
      return true;
    }
    if (!(startAtMap[scenario.fileSeq] ?? "").trim()) {
      return true;
    }
    if (!isOriginalDurationZero(scenario.originalDuration)) {
      if (!(durationMap[scenario.fileSeq] ?? "").trim()) {
        return true;
      }
    }
    return false;
  };

  const updateScenario = (updated: DummyStatus) => {
    setScenarios((prev) =>
      prev.map((s) => (s.fileSeq === updated.fileSeq ? updated : s))
    );
  };

  const loadScenarios = async (showToast = false) => {
    try {
      setIsLoadingList(true);
      const response = await fetchDummyScenarios();
      setScenarios(response);
      // 사용자가 아직 손대지 않은 행에만 디폴트 duration 주입
      setDurationMap((prev) => {
        const next: Record<number, string> = { ...prev };
        for (const scenario of response) {
          if (next[scenario.fileSeq] === undefined) {
            next[scenario.fileSeq] = safeIsoToHHMM(scenario.originalDuration);
          }
        }
        return next;
      });
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

  const handleUploadClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileSelected = async (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    // 같은 파일을 다시 선택할 수 있도록 input 값 리셋
    e.target.value = "";
    if (!file) {
      return;
    }

    try {
      setIsUploading(true);
      const response = await uploadDummyScenario(file);
      toast.success(`${response.scenarioFile} 업로드 완료 (fileSeq=${response.fileSeq})`);
      await loadScenarios(false);
    } catch (err) {
      console.error(err);
      const message = err instanceof Error && err.message ? err.message : "YAML 업로드에 실패했습니다.";
      toast.error(message);
    } finally {
      setIsUploading(false);
    }
  };

  const handlePreview = async (scenario: DummyStatus) => {
    try {
      setPreviewingFileSeq(scenario.fileSeq);
      setPreviewScenario(scenario);
      setPreview(null);
      const response = await fetchDummyPreview(scenario.fileSeq);
      setPreview(response);
    } catch (err) {
      console.error(err);
      toast.error(`${scenario.fileSeq}번 시나리오 미리보기 조회에 실패했습니다.`);
      setPreviewScenario(null);
      setPreview(null);
    } finally {
      setPreviewingFileSeq(null);
    }
  };

  const closePreview = (open: boolean) => {
    if (open) {
      return;
    }
    setPreviewScenario(null);
    setPreview(null);
  };

  const handleInsert = async (scenario: DummyStatus) => {
    const startAt = startAtMap[scenario.fileSeq] ?? "";
    if (!startAt.trim()) {
      toast.error("시작 시각을 입력해주세요.");
      return;
    }

    let durationIso: string | undefined;
    if (!isOriginalDurationZero(scenario.originalDuration)) {
      const durationInput = (durationMap[scenario.fileSeq] ?? "").trim();
      if (!durationInput) {
        toast.error("기간을 입력해주세요.");
        return;
      }
      try {
        durationIso = hhmmToIsoDuration(durationInput);
      } catch {
        toast.error("기간은 HH:MM 형식이어야 합니다.");
        return;
      }
      if (durationIso === "PT0S") {
        toast.error("기간은 0보다 커야 합니다.");
        return;
      }
    }

    try {
      setInsertingFileSeq(scenario.fileSeq);
      const response = await insertDummyScenario(
        scenario.fileSeq,
        toKstOffsetDateTime(startAt),
        durationIso,
      );
      updateScenario({
        fileSeq: response.fileSeq,
        scenarioFile: response.scenarioFile,
        inserted: true,
        appliedStartAt: response.appliedStartAt,
        originalDuration: scenario.originalDuration,
      });
      const appliedDurationLabel = response.appliedDuration
        ? ` · 적용 기간 ${safeIsoToHHMM(response.appliedDuration)}`
        : "";
      toast.success(
        `${response.scenarioFile} 적재 완료 — 시나리오 ${response.insertedScenarioCount}건, ` +
          `게시글 pending ${response.insertedPostPendingCount}건, ` +
          `댓글 pending ${response.insertedCommentPendingCount}건${appliedDurationLabel}`,
      );
    } catch (err) {
      console.error(err);
      toast.error(`${scenario.fileSeq}번 시나리오 적재에 실패했습니다.`);
    } finally {
      setInsertingFileSeq(null);
    }
  };

  const getPreviewStartAt = () => {
    if (!previewScenario) {
      return null;
    }
    const input = startAtMap[previewScenario.fileSeq]?.trim();
    return input ? toKstOffsetDateTime(input) : null;
  };

  const getPreviewOriginalStartAt = () => {
    if (!preview) {
      return null;
    }
    const times = preview.posts.flatMap((post) => [
      post.scheduledAt,
      ...flattenCommentTimes(post.comments),
    ]);
    return times.reduce<string | null>((earliest, current) => {
      if (earliest === null) {
        return current;
      }
      return Date.parse(current) < Date.parse(earliest) ? current : earliest;
    }, null);
  };

  const getAppliedDurationMs = () => {
    if (!preview || !previewScenario) {
      return null;
    }
    const originalDurationMs = safeIsoToMs(preview.originalDuration);
    if (originalDurationMs === 0) {
      return 0;
    }
    const input = (durationMap[previewScenario.fileSeq] ?? "").trim();
    if (!input) {
      return originalDurationMs;
    }
    try {
      return isoDurationToMilliseconds(hhmmToIsoDuration(input));
    } catch {
      return originalDurationMs;
    }
  };

  const getDisplayedScheduledAt = (scheduledAt: string) => {
    if (!preview) {
      return formatAppliedStartAt(scheduledAt);
    }

    const originalStartAt = getPreviewOriginalStartAt();
    const previewStartAt = getPreviewStartAt();
    if (!originalStartAt || !previewStartAt) {
      return formatAppliedStartAt(scheduledAt);
    }

    const originalDurationMs = safeIsoToMs(preview.originalDuration);
    if (originalDurationMs === 0) {
      return formatAppliedStartAt(previewStartAt);
    }

    const appliedDurationMs = getAppliedDurationMs() ?? originalDurationMs;
    const originalOffsetMs = Date.parse(scheduledAt) - Date.parse(originalStartAt);
    const scaledOffsetMs = Math.floor((originalOffsetMs * appliedDurationMs) / originalDurationMs);
    return formatKstDateTime(Date.parse(previewStartAt) + scaledOffsetMs);
  };

  const getPreviewDialogDescription = () => {
    if (!preview) {
      return "시나리오 내용을 불러오는 중입니다.";
    }

    const originalHHMM = safeIsoToHHMM(preview.originalDuration);
    const originalDurationMs = safeIsoToMs(preview.originalDuration);
    const previewStartAt = getPreviewStartAt();

    if (originalDurationMs === 0) {
      const endLabel = previewStartAt
        ? ` · 종료 예상 ${formatAppliedStartAt(previewStartAt)}`
        : "";
      return `원본 기간 ${originalHHMM} (기간 변경 불가)${endLabel}`;
    }

    const appliedDurationMs = getAppliedDurationMs() ?? originalDurationMs;
    const appliedHHMM = minutesToHHMM(Math.floor(appliedDurationMs / 60000));
    const ratio = appliedDurationMs / originalDurationMs;
    const ratioLabel = ratio.toFixed(2);
    const endLabel = previewStartAt
      ? ` · 종료 예상 ${formatKstDateTime(Date.parse(previewStartAt) + appliedDurationMs)}`
      : "";
    return `원본 기간 ${originalHHMM} → 적용 ${appliedHHMM} (${ratioLabel}배)${endLabel}`;
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
                파일별로 시작 시각을 지정해 적재할 수 있습니다. 적재된 파일은 실제 적재 시작 시각을 확인할 수 있습니다.
              </CardDescription>
            </div>
            <div className="flex items-center gap-2">
              <input
                ref={fileInputRef}
                type="file"
                accept=".yml,.yaml,application/x-yaml,text/yaml"
                className="hidden"
                onChange={handleFileSelected}
              />
              <Button
                type="button"
                variant="outline"
                onClick={handleUploadClick}
                disabled={isBusy}
              >
                {isUploading ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  <FileUp className="h-4 w-4" />
                )}
                YAML 업로드
              </Button>
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
          </div>
        </CardHeader>
        <CardContent>
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
                    <TableHead className="w-20">번호</TableHead>
                    <TableHead>파일명</TableHead>
                    <TableHead className="w-24">상태</TableHead>
                    <TableHead className="w-52">시작 시각(KST)</TableHead>
                    <TableHead className="w-36">기간(HH:MM)</TableHead>
                    <TableHead className="w-64 text-right">관리</TableHead>
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
                        {scenario.inserted ? (
                          <span className="text-sm">
                            {scenario.appliedStartAt
                              ? formatAppliedStartAt(scenario.appliedStartAt)
                              : "-"}
                          </span>
                        ) : (
                          <Input
                            type="datetime-local"
                            step={60}
                            value={startAtMap[scenario.fileSeq] ?? ""}
                            onChange={(e) =>
                              setStartAtMap((prev) => ({
                                ...prev,
                                [scenario.fileSeq]: e.target.value,
                              }))
                            }
                            disabled={isBusy}
                            className="h-8 text-sm"
                          />
                        )}
                      </TableCell>
                      <TableCell>
                        {scenario.inserted ? (
                          <span className="text-sm">
                            {safeIsoToHHMM(scenario.originalDuration)}
                          </span>
                        ) : isOriginalDurationZero(scenario.originalDuration) ? (
                          <Input
                            type="text"
                            value="00:00"
                            disabled
                            placeholder="변경 불가"
                            className="h-8 text-sm"
                          />
                        ) : (
                          <Input
                            type="text"
                            value={durationMap[scenario.fileSeq] ?? ""}
                            onChange={(e) =>
                              setDurationMap((prev) => ({
                                ...prev,
                                [scenario.fileSeq]: e.target.value,
                              }))
                            }
                            placeholder="HH:MM"
                            disabled={isBusy}
                            className="h-8 text-sm"
                          />
                        )}
                      </TableCell>
                      <TableCell>
                        <div className="flex justify-end gap-2">
                          <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={() => handlePreview(scenario)}
                            disabled={isBusy}
                          >
                            {previewingFileSeq === scenario.fileSeq ? (
                              <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                              <Eye className="h-4 w-4" />
                            )}
                            미리보기
                          </Button>
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
                            disabled={isInsertDisabled(scenario)}
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
        </CardContent>
      </Card>

      <Dialog open={previewScenario !== null} onOpenChange={closePreview}>
        <DialogContent className="max-h-[90vh] sm:max-w-3xl">
          <DialogHeader>
            <DialogTitle>{previewScenario?.scenarioFile ?? "시나리오"} 미리보기</DialogTitle>
            <DialogDescription>{getPreviewDialogDescription()}</DialogDescription>
          </DialogHeader>
          <div className="max-h-[70vh] space-y-4 overflow-y-auto pr-1">
            {previewingFileSeq !== null && preview === null ? (
              <div className="flex h-40 items-center justify-center text-sm text-muted-foreground">
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                시나리오 내용을 불러오는 중입니다.
              </div>
            ) : preview && preview.posts.length > 0 ? (
              preview.posts.map((post, index) => (
                <PreviewPostCard
                  key={`${post.scheduledAt}-${index}`}
                  post={post}
                  index={index}
                  getDisplayedScheduledAt={getDisplayedScheduledAt}
                />
              ))
            ) : (
              <div className="flex h-40 items-center justify-center text-sm text-muted-foreground">
                표시할 시나리오 내용이 없습니다.
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}

function PreviewPostCard({
  post,
  index,
  getDisplayedScheduledAt,
}: {
  post: DummyPostPreview;
  index: number;
  getDisplayedScheduledAt: (scheduledAt: string) => string;
}) {
  return (
    <div className="rounded-md border p-4">
      <div className="mb-3 flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0">
          <div className="mb-1 flex items-center gap-2 text-sm text-muted-foreground">
            <Database className="h-4 w-4" />
            게시글 {index + 1} · {post.nickname}
          </div>
          <h3 className="break-words text-base font-semibold">{post.title}</h3>
        </div>
        <PreviewTime scheduledAt={getDisplayedScheduledAt(post.scheduledAt)} />
      </div>
      <p className="whitespace-pre-wrap break-words text-sm leading-6">{post.content}</p>
      {post.comments.length > 0 && (
        <div className="mt-4 space-y-3 border-t pt-4">
          {post.comments.map((comment, commentIndex) => (
            <PreviewCommentRow
              key={`${comment.scheduledAt}-${commentIndex}`}
              comment={comment}
              depth={0}
              getDisplayedScheduledAt={getDisplayedScheduledAt}
            />
          ))}
        </div>
      )}
    </div>
  );
}

function PreviewCommentRow({
  comment,
  depth,
  getDisplayedScheduledAt,
}: {
  comment: DummyCommentPreview;
  depth: number;
  getDisplayedScheduledAt: (scheduledAt: string) => string;
}) {
  return (
    <div className="space-y-3" style={{ marginLeft: depth * 18 }}>
      <div className="rounded-md border bg-muted/20 p-3">
        <div className="mb-2 flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-2 text-sm font-medium">
            <MessageSquare className="h-4 w-4 text-muted-foreground" />
            {comment.nickname}
          </div>
          <PreviewTime scheduledAt={getDisplayedScheduledAt(comment.scheduledAt)} />
        </div>
        <p className="whitespace-pre-wrap break-words text-sm leading-6">{comment.content}</p>
      </div>
      {comment.replies.map((reply, replyIndex) => (
        <PreviewCommentRow
          key={`${reply.scheduledAt}-${replyIndex}`}
          comment={reply}
          depth={depth + 1}
          getDisplayedScheduledAt={getDisplayedScheduledAt}
        />
      ))}
    </div>
  );
}

function PreviewTime({ scheduledAt }: { scheduledAt: string }) {
  return (
    <span className="inline-flex shrink-0 items-center gap-1 rounded-md border px-2 py-1 text-xs text-muted-foreground">
      <Clock className="h-3.5 w-3.5" />
      {scheduledAt}
    </span>
  );
}

function flattenCommentTimes(comments: DummyCommentPreview[]): string[] {
  return comments.flatMap((comment) => [
    comment.scheduledAt,
    ...flattenCommentTimes(comment.replies),
  ]);
}

function formatKstDateTime(timestampMs: number) {
  if (Number.isNaN(timestampMs)) {
    return "-";
  }
  const kst = new Date(timestampMs + 9 * 60 * 60 * 1000);
  const year = kst.getUTCFullYear();
  const month = String(kst.getUTCMonth() + 1).padStart(2, "0");
  const date = String(kst.getUTCDate()).padStart(2, "0");
  const hours = String(kst.getUTCHours()).padStart(2, "0");
  const minutes = String(kst.getUTCMinutes()).padStart(2, "0");
  return `${year}-${month}-${date} ${hours}:${minutes}`;
}
