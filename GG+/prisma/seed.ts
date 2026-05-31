import { PrismaClient } from "@prisma/client";
import * as bcrypt from "bcryptjs";

const prisma = new PrismaClient();

async function main() {
  console.log("Cleaning up database...");
  await prisma.featureFlag.deleteMany({});
  await prisma.analytics.deleteMany({});
  await prisma.friendship.deleteMany({});
  await prisma.reaction.deleteMany({});
  await prisma.comment.deleteMany({});
  await prisma.listeningHistory.deleteMany({});
  await prisma.like.deleteMany({});
  await prisma.recommendation.deleteMany({});
  await prisma.trackEmbedding.deleteMany({});
  await prisma.playlistTrack.deleteMany({});
  await prisma.track.deleteMany({});
  await prisma.playlist.deleteMany({});
  await prisma.session.deleteMany({});
  await prisma.account.deleteMany({});
  await prisma.user.deleteMany({});

  console.log("Creating seed users...");
  const salt = await bcrypt.genSalt(10);
  const passwordHash = await bcrypt.hash("password123", salt);

  const adminUser = await prisma.user.create({
    data: {
      name: "Cyber Admin",
      email: "admin@cyber.fm",
      passwordHash,
      role: "ADMIN",
      image: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=200&h=200&fit=crop"
    }
  });

  const normalUser = await prisma.user.create({
    data: {
      name: "Neon Runner",
      email: "neon@cyber.fm",
      passwordHash,
      role: "USER",
      image: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200&h=200&fit=crop"
    }
  });

  const friendUser = await prisma.user.create({
    data: {
      name: "Chrome Shadow",
      email: "shadow@cyber.fm",
      passwordHash,
      role: "USER",
      image: "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?q=80&w=200&h=200&fit=crop"
    }
  });

  console.log("Creating feature flags...");
  await prisma.featureFlag.createMany({
    data: [
      { name: "spotify-sync", description: "Enables exporting playlists directly to Spotify Web API", isActive: true },
      { name: "youtube-fallback", description: "Enables searching YouTube Music as a fallback matching method", isActive: true },
      { name: "realtime-collaboration", description: "Enables real-time collaborative listening rooms", isActive: false },
      { name: "semantic-search", description: "Enables search query embeddings support", isActive: true }
    ]
  });

  console.log("Creating base audio tracks...");
  const trackData = [
    {
      title: "Midnight City",
      artist: "M83",
      album: "Hurry Up, We're Dreaming",
      durationMs: 243000,
      spotifyId: "1KfKVTRH5VjWlWwO4R4594",
      genres: ["synthpop", "indie", "electronic"],
      danceability: 0.72,
      energy: 0.78,
      valence: 0.52,
      tempo: 105.0,
      coverUrl: "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?q=80&w=200&h=200&fit=crop"
    },
    {
      title: "Resonance",
      artist: "Home",
      album: "Odyssey",
      durationMs: 212000,
      spotifyId: "1nT9fQ3484f9WODR333333",
      genres: ["synthwave", "chillwave", "ambient"],
      danceability: 0.65,
      energy: 0.61,
      valence: 0.81,
      tempo: 170.0,
      coverUrl: "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?q=80&w=200&h=200&fit=crop"
    },
    {
      title: "Nightcall",
      artist: "Kavinsky",
      album: "OutRun",
      durationMs: 258000,
      spotifyId: "0FE9t6xyp70gqIIvfiyp1C",
      genres: ["synthwave", "electro", "cyberpunk"],
      danceability: 0.82,
      energy: 0.88,
      valence: 0.39,
      tempo: 116.0,
      coverUrl: "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?q=80&w=200&h=200&fit=crop"
    },
    {
      title: "Turbo Killer",
      artist: "Carpenter Brut",
      album: "Trilogy",
      durationMs: 208000,
      spotifyId: "1234567890abcdefghijkl",
      genres: ["darksynth", "cyberpunk", "electronic"],
      danceability: 0.58,
      energy: 0.96,
      valence: 0.22,
      tempo: 130.0,
      coverUrl: "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=200&h=200&fit=crop"
    },
    {
      title: "After Hours",
      artist: "The Weeknd",
      album: "After Hours",
      durationMs: 361000,
      spotifyId: "2234567890abcdefghijkl",
      genres: ["synthpop", "rnb", "pop"],
      danceability: 0.67,
      energy: 0.72,
      valence: 0.59,
      tempo: 109.0,
      coverUrl: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=200&h=200&fit=crop"
    },
    {
      title: "Cyberpunk 2077 Theme",
      artist: "Marcin Przybyłowicz",
      album: "Cyberpunk 2077 OST",
      durationMs: 310000,
      spotifyId: "3234567890abcdefghijkl",
      genres: ["cyberpunk", "industrial", "orchestral"],
      danceability: 0.45,
      energy: 0.91,
      valence: 0.15,
      tempo: 140.0,
      coverUrl: "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=200&h=200&fit=crop"
    },
    {
      title: "Strobe",
      artist: "deadmau5",
      album: "For Lack of a Better Name",
      durationMs: 637000,
      spotifyId: "4234567890abcdefghijkl",
      genres: ["progressive house", "edm", "trance"],
      danceability: 0.61,
      energy: 0.74,
      valence: 0.35,
      tempo: 128.0,
      coverUrl: "https://images.unsplash.com/photo-1487180142328-0c4e37023af5?q=80&w=200&h=200&fit=crop"
    },
    {
      title: "Intro",
      artist: "The xx",
      album: "xx",
      durationMs: 128000,
      spotifyId: "5234567890abcdefghijkl",
      genres: ["indie pop", "minimal", "ambient"],
      danceability: 0.62,
      energy: 0.38,
      valence: 0.44,
      tempo: 120.0,
      coverUrl: "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?q=80&w=200&h=200&fit=crop"
    }
  ];

  const createdTracks = [];
  for (const track of trackData) {
    const t = await prisma.track.create({
      data: {
        title: track.title,
        artist: track.artist,
        album: track.album,
        durationMs: track.durationMs,
        spotifyId: track.spotifyId,
        genres: track.genres,
        danceability: track.danceability,
        energy: track.energy,
        valence: track.valence,
        tempo: track.tempo,
        coverUrl: track.coverUrl
      }
    });

    // Create seed vector embeddings for semantic matches
    const mockVector = Array.from({ length: 1536 }, () => Math.random() - 0.5);
    await prisma.trackEmbedding.create({
      data: {
        trackId: t.id,
        vector: mockVector
      }
    });

    createdTracks.push(t);
  }

  console.log("Creating seed playlists...");
  const playlist1 = await prisma.playlist.create({
    data: {
      name: "Late Night Coding Stream",
      description: "Atmospheric, punchy cyber-synth beats for typing at 3 AM.",
      coverUrl: "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?q=80&w=400&h=400&fit=crop",
      isPublic: true,
      prompt: "Synthesizers and rhythmic loops for high energy cyber coding",
      moodTags: ["dark", "focused", "energetic"],
      vibe: "cyberpunk",
      energyLevel: 0.85,
      userId: normalUser.id
    }
  });

  const playlist2 = await prisma.playlist.create({
    data: {
      name: "Rainy Vibe Capsule",
      description: "Soft melodies and lo-fi textures for window watching.",
      coverUrl: "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?q=80&w=400&h=400&fit=crop",
      isPublic: true,
      prompt: "Sad rainy night lo-fi chill pop ambient",
      moodTags: ["sad", "calm", "nostalgic"],
      vibe: "lofi",
      energyLevel: 0.32,
      userId: normalUser.id
    }
  });

  console.log("Associating tracks to playlists...");
  await prisma.playlistTrack.create({
    data: { playlistId: playlist1.id, trackId: createdTracks[2].id, order: 0 } // Nightcall
  });
  await prisma.playlistTrack.create({
    data: { playlistId: playlist1.id, trackId: createdTracks[3].id, order: 1 } // Turbo Killer
  });
  await prisma.playlistTrack.create({
    data: { playlistId: playlist1.id, trackId: createdTracks[5].id, order: 2 } // Cyberpunk 2077 Theme
  });

  await prisma.playlistTrack.create({
    data: { playlistId: playlist2.id, trackId: createdTracks[1].id, order: 0 } // Resonance
  });
  await prisma.playlistTrack.create({
    data: { playlistId: playlist2.id, trackId: createdTracks[7].id, order: 1 } // Intro
  });

  console.log("Creating interactions (likes, comments, history)...");
  await prisma.like.create({
    data: { userId: normalUser.id, trackId: createdTracks[2].id } // Nightcall
  });
  await prisma.like.create({
    data: { userId: normalUser.id, trackId: createdTracks[1].id } // Resonance
  });

  await prisma.listeningHistory.create({
    data: { userId: normalUser.id, trackId: createdTracks[0].id, playedAt: new Date(Date.now() - 3600000) }
  });
  await prisma.listeningHistory.create({
    data: { userId: normalUser.id, trackId: createdTracks[2].id, playedAt: new Date(Date.now() - 1800000) }
  });

  await prisma.comment.create({
    data: {
      content: "This playlist keeps me completely locked in while compiling modules. Insane darksynth selections!",
      userId: friendUser.id,
      playlistId: playlist1.id
    }
  });

  await prisma.reaction.create({
    data: { type: "FIRE", userId: friendUser.id, playlistId: playlist1.id }
  });

  console.log("Creating social friendships...");
  await prisma.friendship.create({
    data: { senderId: normalUser.id, receiverId: friendUser.id, status: "ACCEPTED" }
  });

  console.log("Creating analytics history...");
  const moodEvents = ["dark", "gym rage", "rainy sad", "founder energetic"];
  for (let i = 0; i < 20; i++) {
    await prisma.analytics.create({
      data: {
        userId: normalUser.id,
        eventType: "PLAYLIST_GENERATED",
        metadata: {
          mood: moodEvents[i % moodEvents.length],
          tracksCount: Math.floor(Math.random() * 10) + 5,
          vibe: "cyberpunk"
        },
        createdAt: new Date(Date.now() - i * 86400000)
      }
    });
  }

  console.log("Seed script executed successfully.");
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
