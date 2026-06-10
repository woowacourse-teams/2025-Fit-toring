/**
 * SMS Outbox 부하 테스트용 Mock SMS 서버.
 *
 * 실제 Solapi 대신 배치 발송 엔드포인트를 흉내 낸다.
 * - POST /messages/v4/send-many/detail
 * - MOCK_SMS_DELAY_MS(기본 2000ms) 지연 후 전건 성공 응답 {"failedMessageList":[]}
 * - GET /health 로 기동 확인
 *
 * 의존성 없이 Node 내장 http만 사용한다.
 */
const http = require('http');

const port = parseInt(process.env.PORT || '9090', 10);
const delayMs = parseInt(process.env.MOCK_SMS_DELAY_MS || '2000', 10);

const SEND_PATH = '/messages/v4/send-many/detail';

const server = http.createServer((req, res) => {
  if (req.method === 'GET' && req.url === '/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'UP', delayMs }));
    return;
  }

  if (req.method !== 'POST' || req.url !== SEND_PATH) {
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: `unsupported route: ${req.method} ${req.url}` }));
    return;
  }

  let body = '';
  req.on('data', (chunk) => {
    body += chunk;
  });
  req.on('end', () => {
    let messageCount = 0;
    try {
      const parsed = JSON.parse(body);
      messageCount = Array.isArray(parsed.messages) ? parsed.messages.length : 0;
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'invalid JSON body' }));
      return;
    }

    // setTimeout은 논블로킹이므로 지연 중에도 다음 배치 요청을 받을 수 있다.
    setTimeout(() => {
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ failedMessageList: [] }));
      console.log(
        `${new Date().toISOString()} sent=${messageCount} delayMs=${delayMs}`,
      );
    }, delayMs);
  });
});

server.listen(port, () => {
  console.log(`mock-sms listening on :${port}, delayMs=${delayMs}, path=${SEND_PATH}`);
});
