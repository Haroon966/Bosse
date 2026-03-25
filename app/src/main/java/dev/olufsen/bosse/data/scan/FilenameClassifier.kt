package dev.olufsen.bosse.data.scan

import java.util.regex.Pattern

object FilenameClassifier {
    val VIDEO_EXTENSIONS = setOf("mkv", "mp4", "avi", "webm", "m4v", "mov", "wmv", "mpeg", "mpg")

    private val SXE = Pattern.compile("(?i)S(\\d{1,2})\\s*[._\\s-]*\\s*E(\\d{1,2})")
    private val SEASON_FOLDER = Pattern.compile("(?i)^season\\s*(\\d{1,2})$|^s(\\d{1,2})$")
    private val YEAR_PARENS = Pattern.compile("\\((19|20)\\d{2}\\)")

    data class ScanResult(
        val isEpisode: Boolean,
        val seriesTitle: String?,
        val seasonNumber: Int?,
        val episodeNumber: Int?,
        val displayTitle: String,
        val year: Int?,
    )

    /**
     * [pathSegments] are folder names from library root to parent of file (not including file name).
     */
    fun classify(fileName: String, pathSegments: List<String>): ScanResult {
        val base = fileName.substringBeforeLast('.', fileName)
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext !in VIDEO_EXTENSIONS) {
            return ScanResult(
                isEpisode = false,
                seriesTitle = null,
                seasonNumber = null,
                episodeNumber = null,
                displayTitle = base,
                year = extractYear(base, pathSegments),
            )
        }

        val sxe = SXE.matcher(fileName)
        val hasSxe = sxe.find()
        val seasonFromFolder = parseSeasonFolder(pathSegments)

        val isEpisode = hasSxe || seasonFromFolder != null

        if (!isEpisode) {
            val movieTitle = movieTitleFromPath(base, pathSegments)
            return ScanResult(
                isEpisode = false,
                seriesTitle = null,
                seasonNumber = null,
                episodeNumber = null,
                displayTitle = movieTitle,
                year = extractYear(movieTitle, pathSegments),
            )
        }

        val epNum = if (hasSxe) sxe.group(2)!!.toInt() else 1
        val seasonNum = when {
            hasSxe -> sxe.group(1)!!.toInt()
            seasonFromFolder != null -> seasonFromFolder
            else -> 1
        }

        val seriesTitle = inferSeriesTitle(pathSegments, hasSxe, seasonFromFolder != null)
        val epTitle = SXE.matcher(base).replaceAll("").trim { it in "-_. " }
            .ifBlank { "Episode $epNum" }

        return ScanResult(
            isEpisode = true,
            seriesTitle = seriesTitle,
            seasonNumber = seasonNum,
            episodeNumber = epNum,
            displayTitle = epTitle,
            year = null,
        )
    }

    private fun parseSeasonFolder(segments: List<String>): Int? {
        val last = segments.lastOrNull() ?: return null
        val m = SEASON_FOLDER.matcher(last)
        if (!m.find()) return null
        return (m.group(1) ?: m.group(2))?.toInt()
    }

    private fun inferSeriesTitle(
        segments: List<String>,
        hasSxeInFile: Boolean,
        hasSeasonFolder: Boolean,
    ): String {
        return when {
            hasSeasonFolder && segments.size >= 2 ->
                segments[segments.size - 2].trim()
            hasSeasonFolder && segments.size == 1 ->
                segments.first().trim()
            hasSxeInFile && segments.isNotEmpty() ->
                segments.last().trim()
            segments.isNotEmpty() ->
                segments.last().trim()
            else -> "Unknown Series"
        }
    }

    private fun movieTitleFromPath(fileBase: String, segments: List<String>): String {
        val parent = segments.lastOrNull()?.trim().orEmpty()
        val looksLikeMovieFolder = parent.isNotBlank() &&
            (YEAR_PARENS.matcher(parent).find() || segments.size >= 1)
        return if (looksLikeMovieFolder && fileBase.length < 4) {
            YEAR_PARENS.matcher(parent).replaceAll("").trim()
        } else if (looksLikeMovieFolder && parent.isNotBlank()) {
            YEAR_PARENS.matcher(parent).replaceAll("").trim().ifBlank { fileBase }
        } else {
            fileBase
        }
    }

    private fun extractYear(fileBase: String, segments: List<String>): Int? {
        val m = YEAR_PARENS.matcher(fileBase)
        if (m.find()) return m.group().trim('(', ')').toIntOrNull()
        val p = segments.lastOrNull() ?: return null
        val m2 = YEAR_PARENS.matcher(p)
        return if (m2.find()) m2.group().trim('(', ')').toIntOrNull() else null
    }
}
