import type { PropsWithChildren } from 'react';

import { createPortal } from 'react-dom';

const Portal = ({ children }: PropsWithChildren) => {
  return createPortal(children, document.body);
};

export default Portal;
