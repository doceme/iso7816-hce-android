package com.mulum.iso7816hostcardemulation

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class HostCardEmulatorService: HostApduService() {

    companion object {
        private const val TAG = "hce"
        private const val STATUS_SUCCESS = "9000"
        private const val STATUS_FAILED = "6F00"
        private const val CLA_NOT_SUPPORTED = "6E00"
        private const val INS_NOT_SUPPORTED = "6D00"
        private const val AID = "F0010203040506"
        private const val SELECT_INS = "A4"
        private const val DEFAULT_CLA = "00"
        private const val TEST_CLA = "F0"
        private const val MIN_APDU_LENGTH = 12
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex(commandApdu)

        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        Log.d(TAG, "apdu: $hexCommandApdu")

        val cla = hexCommandApdu.substring(0, 2)

        if (cla != DEFAULT_CLA) {
            return if (cla == TEST_CLA) {
                Utils.hexStringToByteArray(AID + STATUS_SUCCESS)
            } else {
                Utils.hexStringToByteArray(CLA_NOT_SUPPORTED)
            }
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        return if (hexCommandApdu.substring(10, 24) == AID)  {
            Utils.hexStringToByteArray(STATUS_SUCCESS)
        } else {
            Utils.hexStringToByteArray(STATUS_FAILED)
        }
    }
}