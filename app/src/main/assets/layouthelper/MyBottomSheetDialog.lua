local bindClass = luajava.bindClass
local BottomSheetDialog = bindClass "com.google.android.material.bottomsheet.BottomSheetDialog"
local loadlayout = require "loadlayout"

return function(context)
  local dialog = BottomSheetDialog(context)
  return {
    setView = function(layout)
      dialog.setContentView(loadlayout(layout))
      dialog.window.decorView.systemUiVisibility = 2
      applyEdgeToEdgePreference(dialog.window, true)
      return dialog
    end
  }
end