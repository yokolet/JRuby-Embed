# calendar.rb [jruby-embed]
# RunScriptletTest, ScriptingContainerTest

require 'date'

class Calendar
  def initialize
    @today = DateTime.now
  end

  def next_year
    @today.year + 1
  end
end
Calendar.new