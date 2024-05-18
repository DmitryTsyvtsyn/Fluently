package io.github.dmitrytsyvtsyn.interfunny.core.navigation

sealed interface Screens {

    data object InterviewEventListScreen : Screens {
        const val name = "interview_event_list"
    }

    data object InterviewEventDetailScreen : Screens {
        const val name = "interview_event_detail"
        const val id = "id"
    }

    data object InterviewThemeSettingsScreen : Screens {
        const val name = "interview_settings"
    }

}