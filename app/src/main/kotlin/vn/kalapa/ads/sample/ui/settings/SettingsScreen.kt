package vn.kalapa.ads.sample.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import vn.kalapa.ads.sample.R
import vn.kalapa.ads.sample.data.AppSettings
import vn.kalapa.ads.sample.data.LanguageChoice
import vn.kalapa.ads.sample.data.MiniAppProfile
import vn.kalapa.ads.sample.ui.theme.SampleSizes
import vn.kalapa.ads.sample.ui.theme.SampleSpacing

/**
 * Host preferences: the mini-app profile and the language used by every app/SDK screen. The mini-app action
 * is docked at the bottom, under the scrolling settings, so the primary action stays in the lower half of the screen.
 */
@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    savedProfile: MiniAppProfile,
    miniAppReady: Boolean,
    onOpenMiniApp: (url: String?) -> Unit,
    onSaveProfile: (MiniAppProfile) -> Unit,
    onApplyProfile: (MiniAppProfile) -> Unit,
    onClearProfile: () -> Unit,
) {
    val context = LocalContext.current
    var language by remember { mutableStateOf(AppSettings.language(context)) }
    var fullName by rememberSaveable { mutableStateOf(savedProfile.fullName) }
    var phoneNumber by rememberSaveable { mutableStateOf(savedProfile.phoneNumber) }
    var userId by rememberSaveable { mutableStateOf(savedProfile.userId) }
    var amount by rememberSaveable { mutableStateOf(savedProfile.amount) }
    var period by rememberSaveable { mutableStateOf(savedProfile.period) }
    val amountValid = remember(amount) { MiniAppProfile(amount = amount).isAmountValid() }

    fun currentProfile() = MiniAppProfile(fullName, phoneNumber, userId, amount, period)

    Column(modifier.fillMaxSize().imePadding()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(SampleSpacing.Two),
            verticalArrangement = Arrangement.spacedBy(SampleSpacing.Three),
        ) {
            Text(
                stringResource(R.string.tab_settings),
                style = MaterialTheme.typography.titleLarge,
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Column(
                    Modifier.padding(SampleSpacing.Two),
                    verticalArrangement = Arrangement.spacedBy(SampleSpacing.Two),
                ) {
                    Text(
                        stringResource(R.string.settings_mini_app_profile),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(stringResource(R.string.profile_full_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small,
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(stringResource(R.string.profile_phone_number)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small,
                    )
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text(stringResource(R.string.profile_user_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small,
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(stringResource(R.string.profile_amount)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small,
                        isError = !amountValid,
                        supportingText = if (amountValid) {
                            null
                        } else {
                            { Text(stringResource(R.string.profile_amount_error)) }
                        },
                    )
                    OutlinedTextField(
                        value = period,
                        onValueChange = { period = it },
                        label = { Text(stringResource(R.string.profile_period)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small,
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth().heightIn(min = SampleSizes.MinTouchTarget),
                        enabled = amountValid,
                        onClick = {
                            onApplyProfile(currentProfile())
                        },
                    ) {
                        Text(stringResource(R.string.profile_apply))
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(SampleSpacing.One),
                        verticalArrangement = Arrangement.spacedBy(SampleSpacing.One),
                    ) {
                        OutlinedButton(
                            modifier = Modifier.heightIn(min = SampleSizes.MinTouchTarget),
                            enabled = amountValid,
                            onClick = { onSaveProfile(currentProfile()) },
                        ) {
                            Text(stringResource(R.string.profile_save))
                        }
                        TextButton(
                            modifier = Modifier.heightIn(min = SampleSizes.MinTouchTarget),
                            onClick = {
                                fullName = ""
                                phoneNumber = ""
                                userId = ""
                                amount = ""
                                period = ""
                                onClearProfile()
                            },
                        ) {
                            Text(stringResource(R.string.profile_clear))
                        }
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Column(
                    Modifier.padding(SampleSpacing.Two),
                    verticalArrangement = Arrangement.spacedBy(SampleSpacing.One),
                ) {
                    Text(
                        stringResource(R.string.settings_language),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    if (AppSettings.supportsLanguage) {
                        OptionGroup(
                            options = LanguageChoice.entries,
                            selected = language,
                            label = {
                                when (it) {
                                    LanguageChoice.SYSTEM -> stringResource(R.string.settings_follow_device)
                                    LanguageChoice.VIETNAMESE -> stringResource(R.string.language_vietnamese)
                                    LanguageChoice.ENGLISH -> stringResource(R.string.language_english)
                                }
                            },
                        ) {
                            language = it
                            AppSettings.setLanguage(context, it)
                        }
                    } else {
                        Text(
                            stringResource(R.string.settings_unsupported),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        MiniAppDock(enabled = miniAppReady, onOpen = onOpenMiniApp)
    }
}

@Composable
private fun <T> OptionGroup(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(Modifier.fillMaxWidth().selectableGroup()) {
        options.forEachIndexed { index, option ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = SampleSizes.MinTouchTarget)
                    .selectable(selected = option == selected, role = Role.RadioButton, onClick = { onSelect(option) })
                    .padding(vertical = SampleSpacing.One),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = option == selected, onClick = null)
                Text(
                    label(option),
                    Modifier.padding(start = SampleSpacing.Two),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
