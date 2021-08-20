package com.kotme

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.NetworkErrorException
import android.content.Context
import android.os.Bundle
import android.accounts.AccountManager

import android.content.Intent

class Authenticator(val context: Context) // Simple constructor
    : AbstractAccountAuthenticator(context) {

    // Editing properties is not supported
    override fun editProperties(r: AccountAuthenticatorResponse, s: String): Bundle {
        throw UnsupportedOperationException()
    }

    // Don't add additional accounts
    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle
    ): Bundle {
        val intent = Intent(context, MainActivity::class.java)

        // This key can be anything. Try to use your domain/package
        intent.putExtra(KotmeClient.Origin, accountType)

        // This key can be anything too. It's just a way of identifying the token's type (used when there are multiple permissions)
        intent.putExtra("full_access", authTokenType)

        // This key can be anything too. Used for your reference. Can skip it too.
        intent.putExtra("is_adding_new_account", true)

        // Copy this exactly from the line below.
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    // Ignore attempts to confirm credentials
    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        r: AccountAuthenticatorResponse,
        account: Account,
        bundle: Bundle
    ): Bundle?  = null

    // Getting an authentication token is not supported
    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
            r: AccountAuthenticatorResponse,
            account: Account,
            s: String,
            bundle: Bundle
    ): Bundle {
        throw UnsupportedOperationException()
    }

    // Getting a label for the auth token is not supported
    override fun getAuthTokenLabel(s: String): String {
        throw UnsupportedOperationException()
    }

    // Updating user credentials is not supported
    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
            r: AccountAuthenticatorResponse,
            account: Account,
            s: String,
            bundle: Bundle
    ): Bundle {
        throw UnsupportedOperationException()
    }

    // Checking features for the account is not supported
    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
            r: AccountAuthenticatorResponse,
            account: Account,
            strings: Array<String>
    ): Bundle {
        throw UnsupportedOperationException()
    }
}