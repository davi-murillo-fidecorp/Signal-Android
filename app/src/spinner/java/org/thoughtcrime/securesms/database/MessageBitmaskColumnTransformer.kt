package org.thoughtcrime.securesms.database

import android.database.Cursor
import org.signal.spinner.ColumnTransformer
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BAD_DECRYPT_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_DRAFT_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_INBOX_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_OUTBOX_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_PENDING_INSECURE_SMS_FALLBACK
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_PENDING_SECURE_SMS_FALLBACK
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_SENDING_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_SENT_FAILED_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_SENT_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BASE_TYPE_MASK
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.BOOST_REQUEST_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.CHANGE_NUMBER_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.ENCRYPTION_REMOTE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.ENCRYPTION_REMOTE_DUPLICATE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.ENCRYPTION_REMOTE_FAILED_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.ENCRYPTION_REMOTE_LEGACY_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.ENCRYPTION_REMOTE_NO_SESSION_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.END_SESSION_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.EXPIRATION_TIMER_UPDATE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.GROUP_CALL_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.GROUP_LEAVE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.GROUP_UPDATE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.GROUP_V2_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.GROUP_V2_LEAVE_BITS
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.GV1_MIGRATION_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.INCOMING_AUDIO_CALL_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.INCOMING_VIDEO_CALL_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.INVALID_MESSAGE_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.JOINED_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_BUNDLE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_CONTENT_FORMAT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_CORRUPTED_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_IDENTITY_DEFAULT_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_IDENTITY_UPDATE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_IDENTITY_VERIFIED_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.KEY_EXCHANGE_INVALID_VERSION_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.MESSAGE_FORCE_SMS_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.MESSAGE_RATE_LIMITED_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.MISSED_AUDIO_CALL_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.MISSED_VIDEO_CALL_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.OUTGOING_AUDIO_CALL_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.OUTGOING_MESSAGE_TYPES
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.OUTGOING_VIDEO_CALL_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.PROFILE_CHANGE_TYPE
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.PUSH_MESSAGE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.SECURE_MESSAGE_BIT
import org.thoughtcrime.securesms.database.MmsSmsColumns.Types.UNSUPPORTED_MESSAGE_TYPE

object MessageBitmaskColumnTransformer : ColumnTransformer {

  override fun matches(tableName: String?, columnName: String): Boolean {
    return columnName == "type" || columnName == "msg_box"
  }

  override fun transform(tableName: String?, columnName: String, cursor: Cursor): String {
    val type = cursor.requireLong(columnName)

    val describe = """
      isOutgoingMessageType:${isOutgoingMessageType(type)}
      isForcedSms:${type and MESSAGE_FORCE_SMS_BIT != 0L}
      isDraftMessageType:${type and BASE_TYPE_MASK == BASE_DRAFT_TYPE}
      isFailedMessageType:${type and BASE_TYPE_MASK == BASE_SENT_FAILED_TYPE}
      isPendingMessageType:${type and BASE_TYPE_MASK == BASE_OUTBOX_TYPE || type and BASE_TYPE_MASK == BASE_SENDING_TYPE }
      isSentType:${type and BASE_TYPE_MASK == BASE_SENT_TYPE}
      isPendingSmsFallbackType:${type and BASE_TYPE_MASK == BASE_PENDING_INSECURE_SMS_FALLBACK || type and BASE_TYPE_MASK == BASE_PENDING_SECURE_SMS_FALLBACK}
      isPendingSecureSmsFallbackType:${type and BASE_TYPE_MASK == BASE_PENDING_SECURE_SMS_FALLBACK}
      isPendingInsecureSmsFallbackType:${type and BASE_TYPE_MASK == BASE_PENDING_INSECURE_SMS_FALLBACK}
      isInboxType:${type and BASE_TYPE_MASK == BASE_INBOX_TYPE}
      isJoinedType:${type and BASE_TYPE_MASK == JOINED_TYPE}
      isUnsupportedMessageType:${type and BASE_TYPE_MASK == UNSUPPORTED_MESSAGE_TYPE}
      isInvalidMessageType:${type and BASE_TYPE_MASK == INVALID_MESSAGE_TYPE}
      isBadDecryptType:${type and BASE_TYPE_MASK == BAD_DECRYPT_TYPE}
      isSecureType:${type and SECURE_MESSAGE_BIT != 0L}
      isPushType:${type and PUSH_MESSAGE_BIT != 0L}
      isEndSessionType:${type and END_SESSION_BIT != 0L}
      isKeyExchangeType:${type and KEY_EXCHANGE_BIT != 0L}
      isIdentityVerified:${type and KEY_EXCHANGE_IDENTITY_VERIFIED_BIT != 0L}
      isIdentityDefault:${type and KEY_EXCHANGE_IDENTITY_DEFAULT_BIT != 0L}
      isCorruptedKeyExchange:${type and KEY_EXCHANGE_CORRUPTED_BIT != 0L}
      isInvalidVersionKeyExchange:${type and KEY_EXCHANGE_INVALID_VERSION_BIT != 0L}
      isBundleKeyExchange:${type and KEY_EXCHANGE_BUNDLE_BIT != 0L}
      isContentBundleKeyExchange:${type and KEY_EXCHANGE_CONTENT_FORMAT != 0L}
      isIdentityUpdate:${type and KEY_EXCHANGE_IDENTITY_UPDATE_BIT != 0L}
      isRateLimited:${type and MESSAGE_RATE_LIMITED_BIT != 0L}
      isExpirationTimerUpdate:${type and EXPIRATION_TIMER_UPDATE_BIT != 0L}
      isIncomingAudioCall:${type == INCOMING_AUDIO_CALL_TYPE}
      isIncomingVideoCall:${type == INCOMING_VIDEO_CALL_TYPE}
      isOutgoingAudioCall:${type == OUTGOING_AUDIO_CALL_TYPE}
      isOutgoingVideoCall:${type == OUTGOING_VIDEO_CALL_TYPE}
      isMissedAudioCall:${type == MISSED_AUDIO_CALL_TYPE}
      isMissedVideoCall:${type == MISSED_VIDEO_CALL_TYPE}
      isGroupCall:${type == GROUP_CALL_TYPE}
      isGroupUpdate:${type and GROUP_UPDATE_BIT != 0L}
      isGroupV2:${type and GROUP_V2_BIT != 0L}
      isGroupQuit:${type and GROUP_LEAVE_BIT != 0L && type and GROUP_V2_BIT == 0L}
      isChatSessionRefresh:${type and ENCRYPTION_REMOTE_FAILED_BIT != 0L}
      isDuplicateMessageType:${type and ENCRYPTION_REMOTE_DUPLICATE_BIT != 0L}
      isDecryptInProgressType:${type and 0x40000000 != 0L}
      isNoRemoteSessionType:${type and ENCRYPTION_REMOTE_NO_SESSION_BIT != 0L}
      isLegacyType:${type and ENCRYPTION_REMOTE_LEGACY_BIT != 0L || type and ENCRYPTION_REMOTE_BIT != 0L}
      isProfileChange:${type == PROFILE_CHANGE_TYPE}
      isGroupV1MigrationEvent:${type == GV1_MIGRATION_TYPE}
      isChangeNumber:${type == CHANGE_NUMBER_TYPE}
      isBoostRequest:${type == BOOST_REQUEST_TYPE}
      isGroupV2LeaveOnly:${type and GROUP_V2_LEAVE_BITS == GROUP_V2_LEAVE_BITS}
    """.trimIndent()

    return "$type<br><br>" + describe.replace(Regex("is[A-Z][A-Za-z0-9]*:false\n?"), "").replace("\n", "<br>")
  }

  private fun isOutgoingMessageType(type: Long): Boolean {
    for (outgoingType in OUTGOING_MESSAGE_TYPES) {
      if (type and BASE_TYPE_MASK == outgoingType) return true
    }
    return false
  }
}
