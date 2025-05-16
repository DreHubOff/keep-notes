package com.jksol.keep.notes.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainCheckList(item: MainScreenItem.CheckList) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = item.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ChecklistItems(item)
            if (item.isOverfilled) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.ticked_items_counter).format(item.tickedItems),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ChecklistItems(item: MainScreenItem.CheckList) {
    Column(
        modifier = Modifier.padding(horizontal = 6.dp),
    ) {
        item.items.forEach { checkListItem ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    modifier = Modifier.height(34.dp),
                    checked = checkListItem.isChecked,
                    onCheckedChange = {

                    },
                    enabled = false,
                )
                Text(
                    text = checkListItem.text,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val demoCheckList = MainScreenItem.CheckList(
        title = "Grocery List",
        content = "Things to buy this weekend",
        items = listOf(
            MainScreenItem.CheckList.Item(isChecked = true, text = "Milk"),
            MainScreenItem.CheckList.Item(isChecked = false, text = "Eggs"),
            MainScreenItem.CheckList.Item(isChecked = true, text = "Bread")
        ),
        tickedItems = 2
    )
    ApplicationTheme {
        MainCheckList(demoCheckList)
    }
}