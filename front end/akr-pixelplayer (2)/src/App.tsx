import { useState, useEffect } from 'react';
import { Github, Play, Download, Music, Sparkles, Layers, Menu, X, ListMusic } from 'lucide-react';
import TechStackGuide from './components/TechStackGuide';
import PixelFooter from './components/PixelFooter';
import { motion, AnimatePresence } from 'motion/react';

export default function App() {
  const [currentScreenIndex, setCurrentScreenIndex] = useState(0);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [videoMounted, setVideoMounted] = useState(false);
  const [videoOpacity, setVideoOpacity] = useState(0);

  const appScreens = [
    { title: "Home / Discover", path: new URL('../images/screenshot1.jpg', import.meta.url).href },
    { title: "Now Playing (Waveform)", path: new URL('../images/screenshot2.jpg', import.meta.url).href },
    { title: "Library Tracks", path: new URL('../images/screenshot3.jpg', import.meta.url).href },
    { title: "Now Playing (Treachery)", path: new URL('../images/WhatsApp Image 2026-06-08 at 18.50.45.jpeg', import.meta.url).href },
    { title: "Playlist Details", path: new URL('../images/WhatsApp Image 2026-06-08 at 18.50.46.jpeg', import.meta.url).href },
    { title: "Search Input View", path: new URL('../images/WhatsApp Image 2026-06-08 at 18.50.46 (2).jpeg', import.meta.url).href },
    { title: "Playlist Import Modal", path: new URL('../images/WhatsApp Image 2026-06-08 at 18.50.46 (1).jpeg', import.meta.url).href }
  ];

  const categories = [
    { name: 'Local Playback', desc: 'Gapless, high-fidelity playback powered by Media3 ExoPlayer & FFmpeg.', icon: <Play className="w-6 h-6" /> },
    { name: 'Import Playlists', desc: 'Import playlists seamlessly from Spotify, YouTube Music, and local M3U/PLS files.', icon: <ListMusic className="w-6 h-6" /> },
    { name: 'Wear OS Sync', desc: 'Dynamic routing and standalone offline playback directly on your wrist.', icon: <Layers className="w-6 h-6" /> },
    { name: 'AI Playlists', desc: 'Natural language prompts turn into perfectly curated mixes translated in real-time.', icon: <Sparkles className="w-6 h-6" /> }
  ];

  // Auto cycle screens every 4 seconds unless clicked
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentScreenIndex((prevIndex) => (prevIndex + 1) % appScreens.length);
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  // Defer mounting background video for LCP optimization and trigger fade-in
  useEffect(() => {
    setVideoMounted(true);
    const timer = setTimeout(() => {
      setVideoOpacity(0.5);
    }, 150);
    return () => clearTimeout(timer);
  }, []);

  const handleNextScreen = () => {
    setCurrentScreenIndex((prevIndex) => (prevIndex + 1) % appScreens.length);
  };

  return (
    <div className="min-h-screen text-white font-sans flex flex-col relative">

      {/* Top Notification Banner */}
      <div className="w-full bg-white text-black py-2.5 px-4 flex items-center justify-center gap-3 text-xs md:text-sm font-bold relative z-30 shadow-md">
        <span className="tracking-tight">Try Ad-free, privacy-focused YouTube</span>
        <a
          href="https://github.com/ajaykumarreddy-k/AKR-Music-Mix/releases"
          target="_blank"
          rel="noreferrer"
          className="bg-black hover:bg-zinc-800 text-white px-4 py-1 rounded-full text-xs font-black transition-all duration-300 shadow-sm"
        >
          Download AKR - Pixel Player
        </a>
      </div>

      {/* Sticky Scroll-Linked Header */}
      <header className="sticky-header">
        <motion.header
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
          className="relative z-20 flex justify-between items-center px-6 md:px-12 py-3.5 w-full max-w-[1400px] mx-auto"
        >
          <div className="text-lg md:text-xl font-black tracking-wider uppercase flex items-center gap-2.5">
            <div className="w-8 h-8 bg-white/10 backdrop-blur-md rounded-xl flex items-center justify-center border border-white/20 overflow-hidden">
              <img src="/Music Logo .png" className="w-full h-full object-cover" alt="AKR Logo" />
            </div>
            <span>AKR - Pixel Player</span>
          </div>

          {/* Desktop Menu with CSS Active Scroll Highlighting */}
          <nav className="hidden md:flex items-center gap-8 text-xs font-bold tracking-widest uppercase">
            <a href="#about" className="nav-link" style={{ '--an': '--about', '--at': '--about-s' } as any}>Home</a>
            <a href="#features" className="nav-link" style={{ '--an': '--features', '--at': '--features-s' } as any}>Features</a>
            <a href="#playlists" className="nav-link" style={{ '--an': '--playlists', '--at': '--playlists-s' } as any}>Playlists</a>
            <a href="#about-details" className="nav-link" style={{ '--an': '--about-details', '--at': '--details-s' } as any}>About</a>
            <a href="#stack" className="nav-link" style={{ '--an': '--stack', '--at': '--stack-s' } as any}>Tech Stack</a>
          </nav>

          <div className="flex items-center gap-4">
            <a
              href="https://github.com/ajaykumarreddy-k/AKR-PixelPlayer"
              target="_blank"
              rel="noreferrer"
              className="hidden sm:flex items-center justify-center px-5 py-2.5 rounded-full border border-white/15 bg-white/5 hover:bg-white/10 hover:border-white/30 transition-all font-bold text-xs tracking-wider uppercase"
            >
              <Music className="w-3.5 h-3.5 mr-2" /> Source
            </a>

            {/* Mobile Menu Button */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 text-white hover:bg-white/10 rounded-lg transition-colors"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </motion.header>

        {/* Scroll-driven Reading Progress Bar */}
        <div className="read-progress-bar"></div>
      </header>

      {/* Mobile Menu Overlay */}
      <AnimatePresence>
        {mobileMenuOpen && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="fixed top-[64px] left-0 w-full bg-[#0b0f19]/95 backdrop-blur-xl border-b border-white/10 z-40 px-6 py-8 flex flex-col gap-6 md:hidden text-center"
          >
            <a
              href="#about"
              onClick={() => setMobileMenuOpen(false)}
              className="text-lg font-bold tracking-wider text-white/80 hover:text-white transition-colors"
            >
              Home
            </a>
            <a
              href="#features"
              onClick={() => setMobileMenuOpen(false)}
              className="text-lg font-bold tracking-wider text-white/80 hover:text-white transition-colors"
            >
              Features
            </a>
            <a
              href="#playlists"
              onClick={() => setMobileMenuOpen(false)}
              className="text-lg font-bold tracking-wider text-white/80 hover:text-white transition-colors"
            >
              Playlists
            </a>
            <a
              href="#about-details"
              onClick={() => setMobileMenuOpen(false)}
              className="text-lg font-bold tracking-wider text-white/80 hover:text-white transition-colors"
            >
              About
            </a>
            <a
              href="#stack"
              onClick={() => setMobileMenuOpen(false)}
              className="text-lg font-bold tracking-wider text-white/80 hover:text-white transition-colors"
            >
              Tech Stack
            </a>
            <a
              href="https://github.com/ajaykumarreddy-k/AKR-PixelPlayer"
              target="_blank"
              rel="noreferrer"
              className="flex items-center justify-center py-3 rounded-full border border-white/15 bg-white/5 font-bold text-sm tracking-wider uppercase mt-2"
            >
              <Github className="w-4 h-4 mr-2" /> GitHub Source
            </a>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Hero Wrapper (Hero Section + Video BG) */}
      <div className="relative w-full min-h-[calc(100vh-44px)] flex flex-col justify-between overflow-hidden">

        {/* Hero Video Background */}
        <div className="absolute inset-0 overflow-hidden -z-10 bg-[#0b0f19]">
          {videoMounted && (
            <video
              className="w-full h-full object-cover filter brightness-90 scale-100 transition-opacity duration-1000"
              style={{ opacity: videoOpacity }}
              autoPlay
              loop
              muted
              playsInline
              src={new URL('../Pretty High Protein Lunch Ideas Worth Trying - Pin-988188343267502610.mp4', import.meta.url).href}
            />
          )}
          <div className="absolute inset-0 bg-gradient-to-b from-transparent via-[#0b0f19]/30 to-[#0b0f19]"></div>
        </div>

        {/* Main Landing / Hero Area */}
        <section id="about" style={{ '--timeline': '--about-s' } as any} className="relative z-10 flex-1 flex flex-col justify-center py-12 md:py-20 max-w-[1400px] mx-auto px-6 w-full">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-16 lg:gap-8 items-center w-full my-auto">

            {/* Left Column: Heading, Description, QR Code, & Download Buttons */}
            <motion.div
              initial={{ opacity: 0, x: -30 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.8, delay: 0.2 }}
              className="flex flex-col justify-center lg:items-start text-center lg:text-left space-y-8"
            >
              <div className="space-y-4">
                <h2 className="text-5xl md:text-7xl xl:text-8xl font-black tracking-tighter leading-[0.9] text-white text-glow">
                  Enjoy the <br className="hidden lg:block" /> Music
                </h2>
                <p className="text-xl md:text-2xl font-medium text-white/60 tracking-wide">
                  Completely Adfree
                </p>
              </div>

              <div className="flex flex-col items-center lg:items-start gap-8 pt-4 w-full">
                {/* Translucent QR Box */}
                <div className="glass-panel p-6 rounded-[2.5rem] flex items-center justify-center w-48 h-48 border border-white/10 hover:border-white/20 transition-colors shadow-2xl">
                  <div className="bg-white p-3 rounded-2xl shadow-inner flex items-center justify-center w-36 h-36">
                    <img
                      className="w-28 h-28 object-contain"
                      src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&color=000000&bgcolor=ffffff&data=https://github.com/ajaykumarreddy-k/AKR-Music-Mix/releases/latest"
                      alt="Download Link QR Code"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                        const parent = e.currentTarget.parentElement;
                        if (parent) {
                          const fallback = parent.querySelector('.qr-fallback');
                          if (fallback) fallback.classList.remove('hidden');
                        }
                      }}
                    />
                    <div className="qr-fallback hidden text-black text-center font-bold text-xs tracking-tight">
                      <Music className="w-8 h-8 mx-auto mb-1 text-[#FF4200]" />
                      Scan to <br /> Download
                    </div>
                  </div>
                </div>

                {/* Download Button */}
                <a
                  href="https://github.com/ajaykumarreddy-k/AKR-Music-Mix/releases/latest"
                  target="_blank"
                  rel="noreferrer"
                  className="bg-white hover:bg-zinc-200 text-black px-6 py-3.5 rounded-2xl flex items-center justify-between gap-4 w-64 md:w-68 shadow-[0_15px_30px_rgba(0,0,0,0.5)] transition-all duration-300 transform hover:scale-[1.02] active:scale-[0.98] group/dl"
                >
                  <div className="flex items-center gap-3.5">
                    <div className="w-9 h-9 rounded-xl bg-black/5 flex items-center justify-center text-black group-hover/dl:bg-black/10 transition-colors">
                      <Download className="w-4.5 h-4.5" />
                    </div>
                    <div className="flex items-center">
                      <span className="font-extrabold text-sm tracking-tight text-zinc-900">Download Now</span>
                    </div>
                  </div>
                </a>

                {/* Alternative Links */}
                <div className="flex gap-4">
                  <a
                    href="https://github.com/ajaykumarreddy-k/AKR-PixelPlayer"
                    target="_blank"
                    rel="noreferrer"
                    className="w-12 h-12 rounded-full glass-panel flex items-center justify-center hover:bg-white/15 hover:scale-105 active:scale-95 transition-all duration-300 shadow-lg"
                  >
                    <Github className="w-5 h-5 text-white" />
                  </a>
                  <a
                    href="#experiments"
                    className="w-12 h-12 rounded-full glass-panel flex items-center justify-center hover:bg-white/15 hover:scale-105 active:scale-95 transition-all duration-300 shadow-lg"
                  >
                    <Play className="w-5 h-5 text-white ml-0.5" fill="currentColor" />
                  </a>
                </div>
              </div>
            </motion.div>

            {/* Center Column: Left empty to show background video */}
            <div className="hidden lg:block"></div>

            {/* Right Column: Phone Mockup */}
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ duration: 0.8 }}
              className="flex flex-col items-center lg:items-end justify-center relative z-10"
            >
              {/* Phone Glow Background */}
              <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-64 h-64 bg-white/5 rounded-full blur-[80px] -z-10 pointer-events-none animate-pulse"></div>

              {/* Realistic Phone Shell */}
              <div className="relative w-[280px] h-[570px] md:w-[310px] md:h-[630px] bg-black rounded-[45px] p-3.5 shadow-[0_30px_70px_rgba(0,0,0,0.8),0_0_0_12px_rgba(30,30,36,0.95)] border border-white/10 flex items-center justify-center overflow-hidden group/phone transition-transform duration-500 hover:scale-[1.02]">
                {/* Dynamic Island */}
                <div className="absolute top-4 left-1/2 -translate-x-1/2 w-28 h-5.5 bg-black rounded-full z-30 flex items-center justify-center">
                  <div className="w-10 h-0.75 bg-zinc-800 rounded-full"></div>
                </div>

                {/* Screen Area */}
                <div className="relative w-full h-full rounded-[34px] overflow-hidden bg-zinc-950 flex flex-col">
                  <img
                    src={appScreens[currentScreenIndex].path}
                    alt={appScreens[currentScreenIndex].title}
                    className="w-full h-full object-cover select-none cursor-pointer active:scale-95 transition-all duration-300"
                    onClick={handleNextScreen}
                  />

                  {/* Visual indicator for click */}
                  <div className="absolute bottom-12 left-1/2 -translate-x-1/2 px-4 py-2 bg-black/80 backdrop-blur-md rounded-full text-[10px] text-white/80 border border-white/10 opacity-0 group-hover/phone:opacity-100 transition-opacity duration-300 pointer-events-none tracking-widest uppercase font-bold shadow-lg">
                    Tap Screen to Cycle
                  </div>
                </div>
              </div>

              {/* Pagination Indicators */}
              <div className="flex items-center gap-1.5 mt-6">
                {appScreens.map((screen, idx) => (
                  <button
                    key={idx}
                    onClick={() => setCurrentScreenIndex(idx)}
                    className={`h-1.5 rounded-full transition-all duration-300 ${idx === currentScreenIndex ? 'w-5 bg-white' : 'w-1.5 bg-white/30 hover:bg-white/50'
                      }`}
                    title={screen.title}
                  />
                ))}
              </div>

              <p className="text-white/40 text-[10px] font-bold mt-2.5 tracking-widest uppercase">
                {appScreens[currentScreenIndex].title}
              </p>
            </motion.div>

          </div>
        </section>

      </div>

      {/* Supported Playlists / Shutter Tiles Reveal Section */}
      {/* Features Section */}
      <section id="features" style={{ '--timeline': '--features-s' } as any} className="relative z-10 w-full border-t border-white/10 bg-black/40 backdrop-blur-2xl py-24 md:py-32">
        <div className="max-w-[1400px] mx-auto px-6 flex flex-col gap-16">

          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
            <div className="max-w-2xl space-y-4">
              <motion.h2
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-100px" }}
                transition={{ duration: 0.8 }}
                className="text-4xl md:text-6xl font-black tracking-tighter text-white"
              >
                Features
              </motion.h2>
              <motion.p
                initial={{ opacity: 0 }}
                whileInView={{ opacity: 1 }}
                viewport={{ once: true, margin: "-100px" }}
                transition={{ duration: 0.8, delay: 0.2 }}
                className="text-lg md:text-xl text-white/60 leading-relaxed tracking-tight"
              >
                For AKR - Pixel Player, every experiment begins with a bold idea: How can a music player be more intelligent? Pushing the boundaries of mobile audio, it brings these ideas to life by uniting local bit-perfect playback with global streaming capabilities.
              </motion.p>
            </div>

            <motion.a
              initial={{ opacity: 0, scale: 0.95 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: 0.4 }}
              href="https://github.com/ajaykumarreddy-k/AKR-Music-Mix/releases/latest"
              target="_blank"
              rel="noreferrer"
              className="flex items-center justify-center gap-3 bg-white text-black hover:bg-zinc-200 px-8 py-4 rounded-2xl font-bold text-sm tracking-wide shadow-xl self-start md:self-auto transition-all duration-300"
            >
              <Download className="w-4 h-4" /> Download APK
            </motion.a>
          </div>

          {/* Bento Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {categories.map((cat, i) => (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-50px" }}
                transition={{ duration: 0.6, delay: i * 0.1 }}
                key={i}
                className="glass-panel glass-panel-hover flex flex-col justify-between p-8 rounded-[2rem] min-h-[300px]"
              >
                <div className="flex justify-between items-start w-full">
                  <div className="w-12 h-12 rounded-xl bg-white/10 flex items-center justify-center text-white border border-white/15 shadow-inner">
                    {cat.icon}
                  </div>
                  <span className="text-4xl font-black text-white/5 select-none">0{i + 1}</span>
                </div>
                <div className="mt-8 space-y-3">
                  <h3 className="text-2xl font-bold tracking-tight text-white">{cat.name}</h3>
                  <p className="text-white/60 text-sm leading-relaxed">{cat.desc}</p>
                </div>
              </motion.div>
            ))}
          </div>

        </div>
      </section>

      {/* Supported Playlists / Platforms Section */}
      <section id="playlists" style={{ '--timeline': '--playlists-s' } as any} className="relative z-10 w-full border-t border-[#223521]/15 bg-[#FAF9F6] py-24 md:py-32">
        <div className="max-w-[1400px] mx-auto px-6 flex flex-col gap-16">
          
          <div className="max-w-4xl text-left">
            <motion.h4
              initial={{ opacity: 0, y: 10 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              className="text-xs font-black tracking-widest uppercase text-[#223521] bg-[#223521]/5 border border-[#223521]/10 px-4 py-2 rounded-full inline-block shadow-sm mb-4"
            >
              Playlist Source Sync
            </motion.h4>
            <motion.h2
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6 }}
              className="text-4xl md:text-6xl font-black tracking-tighter text-[#223521] mb-4"
            >
              Supported Platforms
            </motion.h2>
            <motion.p
              initial={{ opacity: 0 }}
              whileInView={{ opacity: 1 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: 0.2 }}
              className="text-base md:text-lg text-[#223521]/70 max-w-xl font-medium"
            >
              Stream ad-free from YouTube or import your music library playlists seamlessly from your favorite streaming services.
            </motion.p>
          </div>

          {/* Platform Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 w-full">

            {/* YouTube Music - ACTIVE */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5 }}
              className="bg-white border border-[#223521]/10 p-8 rounded-[2.5rem] flex flex-col justify-between min-h-[320px] shadow-[0_4px_30px_rgba(34,53,33,0.02)] hover:border-red-500/30 hover:shadow-[0_20px_50px_rgba(239,68,68,0.05)] transition-all duration-300 group/yt"
            >
              <div className="flex justify-between items-start">
                <div className="w-14 h-14 rounded-2xl bg-red-500/10 flex items-center justify-center border border-red-500/20 group-hover/yt:scale-105 transition-transform duration-300">
                  <svg viewBox="0 0 24 24" className="w-7 h-7 text-red-500" fill="currentColor">
                    <path d="M23.498 6.163a3.003 3.003 0 0 0-2.11-2.107C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.388.511a3.002 3.002 0 0 0-2.11 2.107A30.082 30.082 0 0 0 0 12a30.082 30.082 0 0 0 .502 5.837 3.002 3.002 0 0 0 2.11 2.107c1.883.511 9.388.511 9.388.511s7.505 0 9.388-.511a3.002 3.002 0 0 0 2.11-2.107A30.082 30.082 0 0 0 24 12a30.082 30.082 0 0 0-.502-5.837zM9.545 15.568V8.432L15.818 12l-6.273 3.568z" />
                  </svg>
                </div>
                <span className="flex items-center gap-1.5 text-[10px] font-extrabold uppercase tracking-widest text-red-500 bg-red-500/10 border border-red-500/20 px-3 py-1 rounded-full shadow-sm">
                  <span className="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse"></span>
                  Active
                </span>
              </div>
              <div className="space-y-3 mt-10">
                <h3 className="text-2xl font-bold tracking-tight text-[#223521]">YouTube Music</h3>
                <p className="text-[#223521]/70 text-xs leading-relaxed font-medium">
                  Enjoy privacy-focused, completely ad-free streaming and high-fidelity local caching powered by native extraction.
                </p>
              </div>
            </motion.div>

            {/* Spotify - COMING SOON */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: 0.1 }}
              className="bg-white border border-[#223521]/10 p-8 rounded-[2.5rem] flex flex-col justify-between min-h-[320px] shadow-[0_4px_30px_rgba(34,53,33,0.02)] hover:border-[#1db954]/30 hover:shadow-[0_20px_50px_rgba(29,185,84,0.05)] transition-all duration-300 group/sp"
            >
              <div className="flex justify-between items-start">
                <div className="w-14 h-14 rounded-2xl bg-green-500/5 flex items-center justify-center border border-green-500/10 group-hover/sp:scale-105 transition-transform duration-300">
                  <svg viewBox="0 0 24 24" className="w-7 h-7 text-[#1db954]" fill="currentColor">
                    <path d="M12 0C5.373 0 0 5.372 0 12s5.373 12 12 12 12-5.372 12-12S18.627 0 12 0zm5.495 17.305c-.216.353-.677.465-1.03.247-2.878-1.76-6.5-2.158-10.766-1.185-.403.092-.81-.162-.903-.565-.092-.403.162-.81.565-.903 4.67-1.07 8.665-.623 11.887 1.35.353.218.465.677.247 1.03zm1.468-3.263c-.273.443-.855.584-1.298.31-3.292-2.023-8.31-2.61-12.193-1.432-.497.15-1.023-.13-1.173-.627-.15-.497.13-1.024.627-1.173 4.438-1.348 9.96-.69 13.728 1.625.443.272.584.855.31 1.297zm.126-3.395C15.203 8.32 8.784 8.1 5.02 9.243c-.606.184-1.247-.157-1.43-.763-.184-.606.157-1.247.763-1.43 4.318-1.31 11.41-1.056 16.035 1.688.547.324.727 1.028.403 1.575-.323.547-1.028.727-1.575.403z" />
                  </svg>
                </div>
                <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#223521]/60 bg-[#223521]/5 border border-[#223521]/10 px-3 py-1 rounded-full">
                  Soon
                </span>
              </div>
              <div className="space-y-3 mt-10">
                <h3 className="text-2xl font-bold tracking-tight text-[#223521]">Spotify</h3>
                <p className="text-[#223521]/70 text-xs leading-relaxed font-medium">
                  Import premium playlists and sync offline cached libraries using our intelligent metadata match system.
                </p>
              </div>
            </motion.div>

            {/* Apple Music - COMING SOON */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: 0.2 }}
              className="bg-white border border-[#223521]/10 p-8 rounded-[2.5rem] flex flex-col justify-between min-h-[320px] shadow-[0_4px_30px_rgba(34,53,33,0.02)] hover:border-[#fa243c]/30 hover:shadow-[0_20px_50px_rgba(250,36,60,0.05)] transition-all duration-300 group/ap"
            >
              <div className="flex justify-between items-start">
                <div className="w-14 h-14 rounded-2xl bg-pink-500/5 flex items-center justify-center border border-pink-500/10 group-hover/ap:scale-105 transition-transform duration-300">
                  <svg viewBox="0 0 24 24" className="w-7 h-7 text-[#fa243c]" fill="currentColor">
                    <path d="M19.004 3.005a.75.75 0 0 0-.915-.558l-8 2A.75.75 0 0 0 9.5 6.18v8.625c-.65-.436-1.464-.68-2.35-.68C5.29 14.125 3.75 15.468 3.75 17.125s1.54 3 3.4 3 3.4-1.343 3.4-3V7.93l7-1.75v5.625c-.65-.436-1.464-.68-2.35-.68-1.86 0-3.4 1.343-3.4 3s1.54 3 3.4 3 3.4-1.343 3.4-3V3.75a.747.747 0 0 0-.196-.745z" />
                  </svg>
                </div>
                <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#223521]/60 bg-[#223521]/5 border border-[#223521]/10 px-3 py-1 rounded-full">
                  Soon
                </span>
              </div>
              <div className="space-y-3 mt-10">
                <h3 className="text-2xl font-bold tracking-tight text-[#223521]">Apple Music</h3>
                <p className="text-[#223521]/70 text-xs leading-relaxed font-medium">
                  Full lossless-audio catalog synchronization and playlist recreation using your Apple ID developer connection.
                </p>
              </div>
            </motion.div>

            {/* JioSaavn - COMING SOON */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: 0.3 }}
              className="bg-white border border-[#223521]/10 p-8 rounded-[2.5rem] flex flex-col justify-between min-h-[320px] shadow-[0_4px_30px_rgba(34,53,33,0.02)] hover:border-[#00b2f3]/30 hover:shadow-[0_20px_50px_rgba(0,178,243,0.05)] transition-all duration-300 group/js"
            >
              <div className="flex justify-between items-start">
                <div className="w-14 h-14 rounded-2xl bg-teal-500/5 flex items-center justify-center border border-teal-500/10 group-hover/js:scale-105 transition-transform duration-300">
                  <svg viewBox="0 0 24 24" className="w-7 h-7 text-[#00b2f3]" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <circle cx="12" cy="12" r="10" />
                    <path d="M8 12a4 4 0 0 1 8 0" />
                    <path d="M6 12a6 6 0 0 1 12 0" />
                    <circle cx="12" cy="12" r="1.5" fill="currentColor" />
                  </svg>
                </div>
                <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#223521]/60 bg-[#223521]/5 border border-[#223521]/10 px-3 py-1 rounded-full">
                  Soon
                </span>
              </div>
              <div className="space-y-3 mt-10">
                <h3 className="text-2xl font-bold tracking-tight text-[#223521]">JioSaavn</h3>
                <p className="text-[#223521]/70 text-xs leading-relaxed font-medium">
                  Stream regional tracks and map music choices using local recommendation engines and direct playlist links.
                </p>
              </div>
            </motion.div>

          </div>
        </div>
      </section>

      {/* Dedicated About Section */}
      <section id="about-details" style={{ '--timeline': '--details-s' } as any} className="relative z-10 w-full border-t border-white/10 bg-black/20 backdrop-blur-xl py-20 md:py-28 px-6">
        <div className="max-w-[1400px] mx-auto grid grid-cols-1 lg:grid-cols-2 gap-12 lg:gap-20 items-center">

          {/* Left Column: Heading */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.8 }}
            className="space-y-6"
          >
            <h4 className="text-xs font-black tracking-widest uppercase text-[#dbdc93] bg-white/5 border border-white/10 px-4 py-2 rounded-full inline-block shadow-sm">
              About the Player
            </h4>
            <h2 className="text-4xl md:text-5xl font-black tracking-tighter leading-tight text-white">
              Discover our latest experiments and help shape the future of audio technology.
            </h2>
          </motion.div>

          {/* Right Column: Description */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="space-y-6 text-white/70 text-lg leading-relaxed font-medium"
          >
            <p>
              AKR PixelPlayer bridges the gap between audiophile-grade high-fidelity bit-perfect audio and high-performance cross-device connectivity. Built with local playback at its core, it enables gapless streaming and seamless Wear OS wrist companion synchronization.
            </p>
            <p>
              Get early access to high-res playback, remote Wear OS syncing, and help turn these cutting-edge technologies into products you use every day. Join our community in pushing the limits of modern mobile audio design.
            </p>
          </motion.div>

        </div>
      </section>



      {/* Technical Foundation / Tech Stack */}
      <section id="stack" style={{ '--timeline': '--stack-s' } as any} className="relative z-10 w-full py-24 md:py-32 bg-black/20 border-t border-white/10 px-6">
        <div className="max-w-[1400px] mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.7 }}
            className="text-center mb-16 md:mb-24"
          >
            <h2 className="text-4xl md:text-6xl font-black tracking-tighter text-white mb-4">
              Technical Foundation
            </h2>
            <p className="text-lg md:text-xl text-white/60 max-w-2xl mx-auto tracking-tight">
              Under the hood of our experiments.
            </p>
          </motion.div>
          <TechStackGuide />
        </div>
      </section>

      {/* Creator Spotlight Section */}
      <section id="creator" style={{ '--timeline': '--creator-s' } as any} className="relative z-10 w-full py-24 md:py-32 bg-black/50 border-t border-white/10 px-6 overflow-hidden">
        {/* Glow Effects */}
        <div className="absolute top-1/2 left-1/4 -translate-y-1/2 -translate-x-1/2 w-[400px] h-[400px] rounded-full bg-[#dbdc93]/5 blur-[120px] pointer-events-none"></div>
        
        <div className="max-w-[1400px] mx-auto grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-20 items-center">
          
          {/* Left Column: Visual Profile Card */}
          <motion.div
            initial={{ opacity: 0, x: -30 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            className="lg:col-span-5 w-full"
          >
            <a
              href="https://github.com/ajaykumarreddy-k"
              target="_blank"
              rel="noopener noreferrer"
              className="block glass-panel border-[#dbdc93]/35 bg-gradient-to-br from-[#dbdc93]/15 via-[#0b0f19]/40 to-transparent p-10 rounded-[3rem] shadow-[0_0_50px_rgba(219,220,147,0.06)] hover:border-[#dbdc93]/60 hover:shadow-[0_0_60px_rgba(219,220,147,0.12)] group transition-all duration-300"
            >
              <div className="flex flex-col gap-8">
                {/* SVG Icon & Badge */}
                <div className="flex justify-between items-start">
                  <div className="w-16 h-16 rounded-3xl bg-[#dbdc93]/15 flex items-center justify-center border border-[#dbdc93]/30 group-hover:scale-110 transition-transform duration-300">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-[#dbdc93]">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                    </svg>
                  </div>
                  <span className="text-[11px] font-extrabold uppercase tracking-widest text-[#dbdc93] bg-[#dbdc93]/20 border border-[#dbdc93]/30 px-4 py-1.5 rounded-full animate-pulse">
                    Lead Developer
                  </span>
                </div>

                {/* Profile Details */}
                <div className="space-y-4">
                  <h3 className="text-4xl font-black tracking-tight text-white group-hover:text-[#dbdc93] transition-colors">
                    ajaykumarreddy-k
                  </h3>
                  <p className="text-white/60 text-sm font-medium">
                    Creator of AKR Pixel Player
                  </p>
                </div>

                {/* Follow Button */}
                <div className="pt-4 border-t border-white/5 flex items-center justify-between text-xs font-bold uppercase tracking-wider text-[#dbdc93] group-hover:translate-x-1 transition-transform">
                  <span>View GitHub Profile</span>
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                    <polyline points="12 5 19 12 12 19"></polyline>
                  </svg>
                </div>
              </div>
            </a>
          </motion.div>

          {/* Right Column: Narrative spotlight */}
          <motion.div
            initial={{ opacity: 0, x: 30 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            className="lg:col-span-7 space-y-8 text-left"
          >
            <span className="text-xs font-black tracking-widest uppercase text-[#dbdc93] bg-white/5 border border-white/10 px-4 py-2 rounded-full inline-block shadow-sm">
              The Creator
            </span>
            <h2 className="text-4xl md:text-5xl font-black tracking-tighter leading-tight text-white">
              The Man Behind<br />AKR Pixel Player
            </h2>
            <p className="text-white/80 text-xl font-medium leading-relaxed font-serif-italic max-w-2xl">
              "The lead developer uniting custom local Media3 ExoPlayer engines with automated playlist integrations for the ultimate audio player."
            </p>
            <p className="text-white/60 text-base md:text-lg leading-relaxed font-medium max-w-2xl">
              Focusing on bit-perfect rendering, low-latency audio sync, and modern gesture-driven layout principles to build a seamless player that feels right at home on every device.
            </p>
          </motion.div>

        </div>
      </section>

      {/* Special Thanks & Credits Section */}
      <section id="thanks" style={{ '--timeline': '--thanks-s' } as any} className="relative z-10 w-full py-24 md:py-32 bg-black/40 border-t border-white/10 px-6">
        <div className="max-w-[1400px] mx-auto flex flex-col gap-16">
          
          <div className="max-w-4xl text-left">
            <motion.h4
              initial={{ opacity: 0, y: 10 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              className="text-xs font-black tracking-widest uppercase text-[#dbdc93] bg-white/5 border border-white/10 px-4 py-2 rounded-full inline-block shadow-sm mb-4"
            >
              Credits & Inspiration
            </motion.h4>
            <motion.h2
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6 }}
              className="text-4xl md:text-6xl font-black tracking-tighter text-white mb-4"
            >
              Special Thanks
            </motion.h2>
            <motion.p
              initial={{ opacity: 0 }}
              whileInView={{ opacity: 1 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: 0.2 }}
              className="text-base md:text-lg text-white/60 max-w-xl font-medium"
            >
              Celebrating the exceptional open-source libraries that inspired our player design.
            </motion.p>
          </div>

          {/* Grid Layout */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 w-full">

            {/* PixelPlayer by theovilardo */}
            <motion.a
              href="https://github.com/theovilardo/PixelPlayer"
              target="_blank"
              rel="noopener noreferrer"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5 }}
              className="glass-panel glass-panel-hover p-8 rounded-[2.5rem] flex flex-col justify-between min-h-[300px] group transition-all duration-300"
            >
              <div className="flex justify-between items-start">
                <div className="w-14 h-14 rounded-2xl bg-white/5 flex items-center justify-center border border-white/10 group-hover:scale-105 transition-transform duration-300">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-white">
                    <path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22" />
                  </svg>
                </div>
                <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#dbdc93] bg-[#dbdc93]/10 border border-[#dbdc93]/15 px-3 py-1 rounded-full">
                  Inspiration
                </span>
              </div>
              <div className="space-y-3 mt-10">
                <h3 className="text-2xl font-bold tracking-tight text-white group-hover:text-[#dbdc93] transition-colors">theovilardo / PixelPlayer</h3>
                <p className="text-white/60 text-sm leading-relaxed">
                  A foundational reference for material pixel aesthetics, gapless audio rendering structures, and bit-perfect playback modules.
                </p>
              </div>
            </motion.a>

            {/* Echo Music App */}
            <motion.a
              href="https://github.com/EchoMusicApp"
              target="_blank"
              rel="noopener noreferrer"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: 0.1 }}
              className="glass-panel glass-panel-hover p-8 rounded-[2.5rem] flex flex-col justify-between min-h-[300px] group transition-all duration-300"
            >
              <div className="flex justify-between items-start">
                <div className="w-14 h-14 rounded-2xl bg-white/5 flex items-center justify-center border border-white/10 group-hover:scale-105 transition-transform duration-300">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-white">
                    <circle cx="12" cy="12" r="10" />
                    <path d="M8 12a4 4 0 0 1 8 0" />
                    <path d="M6 12a6 6 0 0 1 12 0" />
                  </svg>
                </div>
                <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#dbdc93] bg-[#dbdc93]/10 border border-[#dbdc93]/15 px-3 py-1 rounded-full">
                  Design Flow
                </span>
              </div>
              <div className="space-y-3 mt-10">
                <h3 className="text-2xl font-bold tracking-tight text-white group-hover:text-[#dbdc93] transition-colors">Echo Music App</h3>
                <p className="text-white/60 text-sm leading-relaxed">
                  Pioneered clean fluid interface principles, sliding gesture navigations, and visual playlist synchronizations that guided this app's UI.
                </p>
              </div>
            </motion.a>

          </div>
        </div>
      </section>

      {/* Footer Section */}
      <PixelFooter />
    </div>
  );
}
