-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 15, 2025 at 02:48 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fikspresif`
--

-- --------------------------------------------------------

--
-- Table structure for table `aspirations`
--

CREATE TABLE `aspirations` (
  `aspiration_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `is_anonymous` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `aspirations`
--

INSERT INTO `aspirations` (`aspiration_id`, `user_id`, `title`, `content`, `is_anonymous`, `created_at`, `updated_at`) VALUES
(16, 7, 'Perbaikan Fasilitas FIK', 'Fasilitas fisik seperti laboratorium, ruang kelas, dan ruang diskusi di Fakultas Ilmu Komputer masih belum optimal. Banyak perangkat laboratorium yang sudah usang atau tidak dapat digunakan maksimal untuk pembelajaran modern seperti pemrograman lanjutan, AI, dan simulasi sistem. Selain itu, kurangnya ruang terbuka atau tempat diskusi membuat mahasiswa kesulitan untuk mengembangkan kerja sama tim dan ide kreatif di luar kelas. Kami mengusulkan peremajaan alat-alat lab, penambahan stop kontak, AC, dan pencahayaan di kelas, serta pembangunan area diskusi yang nyaman dan modern.', 0, '2025-06-10 09:58:21', '2025-06-10 09:58:21'),
(17, 7, 'Stabilkan Akses Internet', 'Internet adalah infrastruktur krusial dalam kegiatan akademik di era digital ini. Sayangnya, koneksi Wi-Fi di lingkungan FIK sering tidak stabil, bahkan tidak bisa diakses di beberapa titik. Ini menyulitkan mahasiswa dalam mengunduh materi, mengikuti kelas daring, atau menjalankan proyek berbasis cloud. Kami mendorong pihak fakultas untuk meningkatkan bandwidth, memperluas jangkauan sinyal Wi-Fi, serta menambah access point di seluruh area kampus untuk mendukung proses belajar yang lancar.', 1, '2025-06-10 09:58:48', '2025-06-10 09:58:48'),
(18, 9, 'Tambah Ruang Diskusi', 'Ruang diskusi merupakan kebutuhan penting bagi mahasiswa dalam mengerjakan proyek kelompok, berdiskusi, dan berkolaborasi secara produktif. Di FIK, ruang seperti ini masih sangat terbatas dan cepat penuh. Akibatnya, banyak mahasiswa terpaksa berdiskusi di lorong, tangga, bahkan di luar gedung yang kurang kondusif. Kami mengusulkan penambahan co-working space yang dilengkapi dengan kursi nyaman, meja besar, colokan listrik, papan tulis, dan koneksi Wi-Fi untuk mendukung atmosfer kerja yang profesional dan kreatif.', 0, '2025-06-10 10:00:27', '2025-06-10 10:00:27'),
(19, 10, 'Kursi Kelas Kurang Nyaman', 'Banyak ruang kelas di FIK masih menggunakan kursi lama yang tidak ergonomis. Kursi yang keras, sempit, atau tidak stabil membuat mahasiswa cepat merasa lelah saat mengikuti perkuliahan, terutama yang berlangsung lebih dari dua jam. Dalam jangka panjang, hal ini bisa berdampak pada kesehatan tubuh dan konsentrasi belajar. Kami berharap pihak fakultas dapat mengganti kursi-kursi tersebut dengan model yang lebih ergonomis, terutama di kelas-kelas yang sering digunakan untuk kuliah reguler.', 0, '2025-06-10 10:02:12', '2025-06-10 10:02:12'),
(20, 10, 'AC Tidak Berfungsi Baik', 'Beberapa ruang kelas dan laboratorium di FIK memiliki pendingin ruangan (AC) yang tidak berfungsi dengan baik. Ruangan menjadi panas, pengap, dan tidak kondusif untuk belajar. Hal ini sangat terasa pada siang hari ketika suhu ruangan meningkat. Selain membuat mahasiswa tidak nyaman, dosen pun kesulitan menyampaikan materi secara optimal. Kami meminta pihak fakultas melakukan pengecekan dan perawatan rutin AC, serta mengganti unit yang sudah tidak layak pakai.', 1, '2025-06-10 10:02:32', '2025-06-10 10:02:32'),
(21, 11, 'Parkiran Mahasiswa Penuh', 'Kapasitas area parkir di FIK sudah tidak mampu menampung jumlah kendaraan mahasiswa yang meningkat setiap tahun. Akibatnya, banyak mahasiswa terpaksa memarkir kendaraan di luar area kampus yang kurang aman dan rawan pencurian. Ini menyebabkan kekhawatiran dan ketidaknyamanan setiap hari. Kami mengusulkan perluasan area parkir mahasiswa, serta penambahan pengamanan dan penerangan di area tersebut agar lebih aman dan tertib.', 0, '2025-06-10 10:04:07', '2025-06-10 10:04:07');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `password`, `full_name`, `created_at`, `updated_at`) VALUES
(1, 'Ridho', 'ridhoUhuy123@co.id', '$2y$10$dg.1COlXCc7Ha8XqpuejquGFHhkTmaPHqCnziwa7bm.S.wbLmlU3e', 'Ridho Dwi Darma Putra', '2025-05-29 11:30:01', '2025-06-08 10:14:24'),
(2, 'Dodol', 'dodol12@co.id', '$2y$10$upuJFIhUUppw8Whn/RMox.BddNkyOijnCVI7oqlc/gV7Sd6lxe8PW', 'Dodol Uhuy', '2025-05-29 11:40:23', '2025-05-29 11:40:23'),
(3, 'Raihan', 'RaihanMadiun@co.id', '$2y$10$ZcUElz8Nzg/jll00k/j0XOP8NiYR/CdpOxpPYOqewh2WhPe4GX01q', 'RaihanMadiun', '2025-05-29 11:44:36', '2025-05-29 11:44:36'),
(4, 'Budi', 'BudiGacor88@co.id', '$2y$10$IxjjoR52IK9VV7yD43/RcujPhu0BEnzt.lk2.V5d5yY1WWln.aQ0y', 'BudiLariPagi', '2025-05-29 11:50:17', '2025-05-29 11:50:17'),
(5, 'Asep', 'asepgacor66@co.id', '$2y$10$rngcjP/WLQzIV1OYF0ov0u5aDEACwDk4PxOZi9MIrLlt1F90mUg7u', 'AsepKnalpot', '2025-05-29 12:13:52', '2025-05-29 12:13:52'),
(6, 'Agus', 'AgusJoki24@co.id', '$2y$10$PFcuV1ZPO11SG9fV8Anv4uRSaKXTBJf1MTX.H6ezM69kPT.kNXNBa', 'Agus Budianto', '2025-05-29 13:44:40', '2025-06-01 21:35:34'),
(7, 'Adit', 'adit76@co.id', '$2y$10$4ur2LvS5et5OdQeT7GpjbeO0SolJMGYQiArM6EFDXzY57lVLBjBgS', 'Adit T nye gede', '2025-06-08 10:52:29', '2025-06-08 14:04:00'),
(8, 'Abdul', 'abdul76@co.id', '$2y$10$5ycLDzZc61rnt8mbEd8Z4u9ty9tQpojvF5WqFaMbpYz.VUAj71jhK', 'Abdul Cihuy', '2025-06-08 14:00:36', '2025-06-08 14:00:36'),
(9, 'jennyaaz', 'jeni@gmail.com', '$2y$10$J/P11sBO7zNHXJTQ06dzruQE9Oyh25TJ8sIftX/QiN/hj3B91rFGy', 'Jenny Aulia', '2025-06-10 09:59:55', '2025-06-10 09:59:55'),
(10, 'lisken', 'lisken@gmail.com', '$2y$10$gqOGnlPNIiFI4WsO8kWqr.2H919HslQYMqF.MJgkBWlm0e0c0iUlu', 'Lisken Ratna', '2025-06-10 10:01:22', '2025-06-10 10:01:22'),
(11, 'raihans', 'raihan@rsz.co.id', '$2y$10$BRO0tzqdnqSh.zsH1/apM.XZpv9PVosllTa7miazZbiJaxqGqEuTi', 'Raihan Shafa', '2025-06-10 10:03:33', '2025-06-10 10:03:33'),
(12, 'fikupnvj', 'fik@upnvj.ac.id', '$2y$10$SWzlUF4i7srnbql/UhjR0OJ9NQN2ElmbAMxJbpsgAwSP3MjVEAl6m', 'FIK UPNVJ', '2025-06-10 14:16:31', '2025-06-10 14:16:31'),
(13, 'daffa', 'daffa77@co.id', '$2y$10$k13/xJSGtNyv8.7JBe.gbesIw8M/eSm13Vi4VIHsJDNfgABn2H422', 'daffa depok', '2025-06-10 15:39:51', '2025-06-10 15:47:08');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `aspirations`
--
ALTER TABLE `aspirations`
  ADD PRIMARY KEY (`aspiration_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `aspirations`
--
ALTER TABLE `aspirations`
  MODIFY `aspiration_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `aspirations`
--
ALTER TABLE `aspirations`
  ADD CONSTRAINT `aspirations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
