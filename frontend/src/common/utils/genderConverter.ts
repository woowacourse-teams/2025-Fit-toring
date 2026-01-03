import type { GenderClient, GenderServer } from '../types/gender';

export function convertGenderClientToServer(gender: GenderClient) {
  const MAPPING: Record<GenderClient, GenderServer> = {
    남: 'MALE',
    여: 'FEMALE',
  } as const;

  return MAPPING[gender];
}

export function convertGenderServerToClient(gender: GenderServer) {
  const MAPPING: Record<GenderServer, GenderClient> = {
    MALE: '남',
    FEMALE: '여',
  } as const;

  return MAPPING[gender];
}
