package gov.wa.wsdot.android.wsdot.ui.ferries
/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.databinding.FerryScheduleItemBinding
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.ui.common.recyclerview.diffcallbacks.FerryScheduleDiffCallback
import gov.wa.wsdot.android.wsdot.util.AppExecutors

/**
 * A RecyclerView adapter for [FerrySchedule] class.
 */
class FerryScheduleListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val scheduleClickCallback: ((FerrySchedule) -> Unit)?, // ClickCallback for item in the adapter
    private val favoriteClickCallback: ((FerrySchedule) -> Unit)?
) : DataBoundListAdapter<FerrySchedule, FerryScheduleItemBinding>(
    appExecutors = appExecutors,
    diffCallback = FerryScheduleDiffCallback()
) {

    override fun createBinding(parent: ViewGroup): FerryScheduleItemBinding {

        val binding = DataBindingUtil.inflate<FerryScheduleItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.ferry_schedule_item,
            parent,
            false,
            dataBindingComponent
        )

        binding.root.findViewById<View>(R.id.tap_view).setOnClickListener {
            binding.schedule?.let {
                scheduleClickCallback?.invoke(it)
            }
        }

        binding.root.findViewById<ImageButton>(R.id.favorite_button).setOnClickListener {
            binding.schedule?.let {
                favoriteClickCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: FerryScheduleItemBinding, item: FerrySchedule, position: Int) {
        binding.schedule = item
    }
}