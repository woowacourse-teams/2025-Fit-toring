import { useEffect, useState } from 'react';

import { priceValidator } from '../utils/priceValidator';

export const usePriceError = (price: number) => {
  const [priceErrorMessage, setPriceErrorMessage] = useState<string>('');

  useEffect(() => {
    setPriceErrorMessage(priceValidator(price));
  }, [price]);

  return { priceErrorMessage };
};
