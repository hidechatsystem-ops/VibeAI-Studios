package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VibeAI", appName)
  }

  @Test
  fun `verify content types have icons and names`() {
    val types = com.example.data.model.ContentType.values()
    assertTrue(types.isNotEmpty())
    types.forEach { type ->
      assertTrue(type.displayName.isNotBlank())
      assertTrue(type.icon.isNotBlank())
    }
  }
}
