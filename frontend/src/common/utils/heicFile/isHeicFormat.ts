const HEIC_SIGNATURES = [
  'heic',
  'heix',
  'hevc',
  'heim',
  'mif1',
  'msf1',
] as const;

export function isHeicFile(file: File): Promise<boolean> {
  return new Promise((resolve) => {
    const reader = new FileReader();

    reader.onloadend = (e) => {
      if (e.target && e.target.result instanceof ArrayBuffer) {
        const buffer = e.target.result;
        const view = new Uint8Array(buffer);

        const signature = String.fromCharCode(...view.slice(4, 12));

        if (HEIC_SIGNATURES.some((sig) => signature.includes(sig))) {
          resolve(true);
          return;
        }
      }

      resolve(false);
    };

    reader.onerror = () => resolve(false);

    const blob = file.slice(0, 12);
    reader.readAsArrayBuffer(blob);
  });
}
