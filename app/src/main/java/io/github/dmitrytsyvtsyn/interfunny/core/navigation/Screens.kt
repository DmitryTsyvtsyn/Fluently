package io.github.dmitrytsyvtsyn.interfunny.core.navigation

sealed interface Screens {

    data object InterviewEventListScreen : Screens {
        const val NAME = "interview_event_list"
    }

    data object InterviewDetailScreen : Screens {
        const val NAME = "interview_event_detail"
        const val ID = "id"
        const val INITIAL_DATE = "initial_date"
    }

    data object InterviewThemeSettingsScreen : Screens {
        const val NAME = "interview_settings"
    }

}