package com.starry.greenstash.ui.screens.input.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarTimeline
import com.starry.greenstash.R
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.screens.input.viewmodels.InputViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.toToast
import com.starry.greenstash.utils.validateAmount
import java.time.format.DateTimeFormatter

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun InputScreen(editGoalId: String?, navController: NavController) {
    val context = LocalContext.current
    val viewModel: InputViewModel = hiltViewModel()

    var imageUri: Any? by remember { mutableStateOf(R.drawable.default_goal_image) }
    val calenderState = rememberSheetState()

    if (editGoalId != null) {
        // TODO
    } else {
        // TODO
    }


    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            imageUri = it
            viewModel.state = viewModel.state.copy(goalImageUri = it)
        }
    }

    CalendarDialog(
        state = calenderState, selection = CalendarSelection.Date { date ->
            viewModel.state = viewModel.state.copy(
                deadline = date.format(
                    DateTimeFormatter.ofPattern(
                        PreferenceUtils.getString(
                            PreferenceUtils.DATE_FORMAT, DateStyle.DateMonthYear.pattern
                        )
                    )
                )
            )
        }, config = CalendarConfig(
            monthSelection = true, yearSelection = true, disabledTimeline = CalendarTimeline.PAST
        )
    )

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
            Text(
                stringResource(id = R.string.input_screen_header),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = null
                )
            }
        })
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(190.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(imageUri)
                                .crossfade(enable = true).build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }


                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp),
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Row {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_image),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.input_pick_image),
                            modifier = Modifier.padding(top = 2.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 30.dp),
                text = stringResource(id = R.string.input_page_quote),
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = viewModel.state.goalTitleText,
                    onValueChange = { newText ->
                        viewModel.state = viewModel.state.copy(goalTitleText = newText)
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    label = {
                        Text(text = stringResource(id = R.string.input_text_title))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_title),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = viewModel.state.targetAmount,
                    onValueChange = { newText ->
                        viewModel.state =
                            viewModel.state.copy(targetAmount = Utils.getValidatedNumber(newText))
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    label = {
                        Text(text = stringResource(id = R.string.input_text_amount))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_amount),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                Spacer(modifier = Modifier.height(18.dp))

                val interactionSource = remember { MutableInteractionSource() }

                OutlinedTextField(
                    value = viewModel.state.deadline,
                    onValueChange = { newText ->
                        viewModel.state = viewModel.state.copy(deadline = newText)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clickable(
                            onClick = {
                                calenderState.show()
                            }, interactionSource = interactionSource, indication = null
                        ),
                    label = {
                        Text(text = stringResource(id = R.string.input_deadline))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_deadline),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.onBackground,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        //For Icons
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = viewModel.state.additionalNotes,
                    onValueChange = { newText ->
                        viewModel.state = viewModel.state.copy(additionalNotes = newText)
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    label = {
                        Text(text = stringResource(id = R.string.input_additional_notes))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_additional_notes),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (viewModel.state.goalTitleText.isEmpty() || viewModel.state.goalTitleText.isBlank()) {
                            context.getString(R.string.title_empty_err).toToast(context)
                        } else if (!viewModel.state.targetAmount.validateAmount()) {
                            context.getString(R.string.amount_empty_err).toToast(context)
                        } else {
                            viewModel.addSavingGoal(context)
                            navController.popBackStack(DrawerScreens.Home.route, true)
                            navController.navigate(DrawerScreens.Home.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.input_save_btn),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

    }
}


@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Preview
@Composable
fun InputScreenPV() {
    InputScreen(editGoalId = null, rememberNavController())
}