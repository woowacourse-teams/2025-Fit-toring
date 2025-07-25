export const smoothScrollTo = (targetY: number, duration = 1000) => {
  const startY = window.scrollY;
  const distance = targetY - startY;
  const start = performance.now();

  const animate = (now: number) => {
    const time = Math.min((now - start) / duration, 1);
    const eased = easeInOutCubic(time);
    window.scrollTo(0, startY + distance * eased);

    if (time < 1) {
      requestAnimationFrame(animate);
    }
  };

  requestAnimationFrame(animate);
};

const easeInOutCubic = (t: number) => {
  return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
};
