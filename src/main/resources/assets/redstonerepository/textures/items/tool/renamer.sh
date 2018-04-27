for i in *; do
  mv "$i" "`echo $i | sed "s/battlewrench/battle_wrench/"`";
done
