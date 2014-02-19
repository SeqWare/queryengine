/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.plugins.recipes;

import com.github.seqware.queryengine.plugins.plugins.*;
import java.io.File;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * This plug-in type implements plug-ins that can be filtered and that output text files.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class FilteredFileOutputPlugin extends PrefilterByAttributesPlugin<Text, Text, Text, Text> {

  @Override
  public Class getMapOutputKeyClass() {
    return Text.class;
  }

  @Override
  public Class getMapOutputValueClass() {
    return Text.class;
  }

  @Override
  public ResultMechanism getResultMechanism() {
    return ResultMechanism.FILE;
  }

  @Override
  public Class<?> getResultClass() {
    return File.class;
  }

  @Override
  public Class<?> getOutputClass() {
    return TextOutputFormat.class;
  }
}
