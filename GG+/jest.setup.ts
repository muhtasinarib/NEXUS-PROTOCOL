// Jest setup file for custom environments
global.fetch = jest.fn();

// Mock logger to avoid cluttering test outputs
jest.mock("@/lib/logger", () => ({
  logger: {
    info: jest.fn(),
    warn: jest.fn(),
    error: jest.fn(),
    debug: jest.fn()
  }
}));
