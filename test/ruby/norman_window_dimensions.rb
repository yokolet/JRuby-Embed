# norman_window_dimensions.rb [jruby-embed]
# jsr223.JRubyEngineTest

def norman_window(x, y)
  puts "Dimensions of a #{x} x #{y} Norman window are"
  puts "area: #{get_area(x, y)}"
  puts "perimeter: #{get_perimeter(x, y)}"
end

def get_area(x, y)
  x * y + Math::PI / 8.0 * x ** 2.0
end

def get_perimeter(x, y)
  x + 2.0 * y + Math::PI / 2.0 * x
end

return norman_window(2, 1), norman_window(1, 2)