# calendar2.rb [jruby-embed]
# jsr223.JRubyEngineTest

require 'date'

class Calendar
  def initialize
    @today = DateTime.now
  end

  def next_year
    @today.year + 1
  end
end
Calendar.new.next_year