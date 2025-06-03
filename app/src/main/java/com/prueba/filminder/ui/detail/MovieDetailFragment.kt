    private fun addGenreChips(genres: List<Genre>) {
        binding.genresGroup.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        
        genres.forEach { genre ->
            val genreView = inflater.inflate(R.layout.item_genre_tag, binding.genresGroup, false) as TextView
            genreView.text = genre.name.uppercase()
            binding.genresGroup.addView(genreView)
        }
    }

    private fun Float.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        ).toInt()
    } 