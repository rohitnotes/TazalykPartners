package kz.nextstep.tazalykpartners.ui.pinComments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kz.nextstep.domain.model.Requests
import kz.nextstep.domain.utils.AppConstants
import kz.nextstep.tazalykpartners.R
import kz.nextstep.tazalykpartners.databinding.RowPinCommentsItemBinding
import kz.nextstep.tazalykpartners.ui.pinDetailedInfo.RequestViewModel
import java.text.SimpleDateFormat
import java.util.*

class PinCommentsAdapter: RecyclerView.Adapter<PinCommentsAdapter.PinCommentsViewHolder>() {

    lateinit var pinCommentsList: MutableList<Requests>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinCommentsViewHolder {
        val binding: RowPinCommentsItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_pin_comments_item, parent, false)
        return PinCommentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (::pinCommentsList.isInitialized) pinCommentsList.size else 0
    }

    fun updatePinCommentsList(pinCommentsList: MutableList<Requests>, size: Int) {
        onSortByDate(pinCommentsList)
        if (size <= pinCommentsList.size) {
            val sortedList: List<Requests> = pinCommentsList.take(size)
            this.pinCommentsList = sortedList.toMutableList()
        } else
            this.pinCommentsList = pinCommentsList
        notifyDataSetChanged()
    }

    private fun onSortByDate(pinCommentsList: MutableList<Requests>){
        pinCommentsList.sortByDescending { SimpleDateFormat(AppConstants.DATE_FORMAT, Locale("ru")).parse(it.comment_date!!) }
    }

    override fun onBindViewHolder(holder: PinCommentsViewHolder, position: Int) {
        holder.bind(pinCommentsList[position])
    }


    class PinCommentsViewHolder(val binding: RowPinCommentsItemBinding): RecyclerView.ViewHolder(binding.root) {
        val viewModel = RequestViewModel()
        fun bind(requests: Requests) {
            viewModel.bind(requests)
            binding.viewModel = viewModel
        }
    }
}