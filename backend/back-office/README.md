# 관리자 백오피스 시스템

웹 환경에 최적화된 관리자 전용 백 오피스 시스템입니다.

## 기능

- 🔐 관리자 로그인 (아이디 방식)
- 📊 대시보드 및 분석
- 👨‍🏫 멘토 관리
- 👨‍🎓 멘티 관리  
- 🎓 멘토링 관리
- 📜 자격증 관리 (조회, 검증, 승인/거절)

## 기술 스택

- **Frontend**: React 18 + TypeScript
- **UI Framework**: Tailwind CSS v4 + shadcn/ui
- **Build Tool**: Vite
- **State Management**: React Hooks
- **HTTP Client**: Fetch API
- **Authentication**: 쿠키 기반 토큰 인증

## 로컬 개발 환경 설정

### 1. 의존성 설치

```bash
# npm 사용
npm install

# 또는 yarn 사용
yarn install

# 또는 pnpm 사용
pnpm install
```

### 2. 개발 서버 실행

```bash
# npm 사용
npm run dev

# 또는 yarn 사용
yarn dev

# 또는 pnpm 사용
pnpm dev
```

개발 서버가 시작되면 브라우저에서 `http://localhost:3000`으로 접속할 수 있습니다.

### 3. 백엔드 서버 설정

이 애플리케이션은 다음 API 엔드포인트를 사용합니다:

- **Base URL**: `http://localhost:8080`
- **로그인**: `POST /login`
- **토큰 재발급**: `POST /reissue`
- **자격증 목록**: `GET /admin/certificates`
- **자격증 상세**: `GET /admin/certificates/{id}`
- **자격증 승인**: `POST /admin/certificates/{id}/approve`
- **자격증 거절**: `POST /admin/certificates/{id}/reject`

백엔드 서버가 `http://localhost:8080`에서 실행되고 있는지 확인하세요.

### 4. 빌드

```bash
# npm 사용
npm run build

# 또는 yarn 사용
yarn build

# 또는 pnpm 사용
pnpm build
```
- 빌드 시 vite 설정으로 인해 fittoring.src.main.resources.static.web-admin 에 빌드 버전이 적용됩니다.

빌드된 파일은 `dist/` 디렉토리에 생성됩니다.

### 5. 프리뷰 (빌드 결과 확인)

```bash
# npm 사용
npm run preview

# 또는 yarn 사용
yarn preview

# 또는 pnpm 사용
pnpm preview
```

## 프로젝트 구조

```
├── components/          # React 컴포넌트
│   ├── dashboard/       # 대시보드 관련 컴포넌트
│   ├── ui/             # shadcn/ui 컴포넌트
│   └── figma/          # Figma 관련 유틸리티
├── services/           # API 서비스 함수
├── styles/            # 글로벌 CSS 스타일
├── App.tsx            # 메인 애플리케이션 컴포넌트
└── main.tsx           # 애플리케이션 엔트리포인트
```

## 환경 변수

현재 API 기본 URL이 하드코딩되어 있습니다. 필요시 환경 변수로 변경할 수 있습니다:

```bash
# .env.local 파일 생성
VITE_API_BASE_URL=http://localhost:8080
```

## 문제 해결

### 포트 충돌
다른 애플리케이션이 3000 포트를 사용 중인 경우:
```bash
npm run dev -- --port 3001
```

### CORS 에러
백엔드 서버에서 CORS 설정을 확인하세요.

### 의존성 에러
node_modules 삭제 후 재설치:
```bash
rm -rf node_modules
npm install
```

## 라이센스

This project is private and proprietary.