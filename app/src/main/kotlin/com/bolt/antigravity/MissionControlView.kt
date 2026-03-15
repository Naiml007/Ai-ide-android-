package com.bolt.antigravity

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bolt.antigravity.databinding.ViewMissionControlBinding

class MissionControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding: ViewMissionControlBinding

    init {
        binding = ViewMissionControlBinding.inflate(LayoutInflater.from(context), this, true)
        orientation = VERTICAL

        binding.agentStatus.text = "AGENT READY"
        binding.agentStatus.setTextColor(Color.CYAN)
    }

    fun setMission(text: String) {
        binding.missionDescription.text = text
    }
}
