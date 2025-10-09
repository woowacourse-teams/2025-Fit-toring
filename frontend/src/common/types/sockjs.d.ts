import 'sockjs-client';

declare module 'sockjs-client' {
  interface Options {
    withCredentials?: boolean;
  }
}
