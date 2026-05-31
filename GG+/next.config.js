/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    domains: [
      "i.scdn.co", // Spotify images
      "images.unsplash.com", // Unsplash fallback covers
      "lh3.googleusercontent.com" // Google profile images
    ]
  },
  experimental: {
    serverComponentsExternalPackages: ["pino"]
  }
};

module.exports = nextConfig;
