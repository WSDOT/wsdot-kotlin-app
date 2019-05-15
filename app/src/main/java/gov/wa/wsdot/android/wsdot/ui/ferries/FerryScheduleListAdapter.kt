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


import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.ui.common.DataBoundListAdapter
import gov.wa.wsdot.android.wsdot.util.AppExecutors

/**
 * A RecyclerView adapter for [FerrySchedule] class.
 */
class FerryScheduleListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val showFullName: Boolean,
    private val repoClickCallback: ((FerrySchedule) -> Unit)?
) : DataBoundListAdapter<FerrySchedule, RepoItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<FerrySchedule>() {
        override fun areItemsTheSame(oldItem: FerrySchedule, newItem: FerrySchedule): Boolean {
            return oldItem.routeId == newItem.routeId
        }

        override fun areContentsTheSame(oldItem: FerrySchedule, newItem: FerrySchedule): Boolean {
            return oldItem.description == newItem.description
        }
    }
) {

    override fun createBinding(parent: ViewGroup): RepoItemBinding {
        val binding = DataBindingUtil.inflate<RepoItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.repo_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.showFullName = showFullName
        binding.root.setOnClickListener {
            binding.repo?.let {
                repoClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: RepoItemBinding, item: FerrySchedule) {
        binding.repo = item
    }
}