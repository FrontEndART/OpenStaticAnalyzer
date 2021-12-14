/*
 *  This file is part of OpenStaticAnalyzer.
 *
 *  Copyright (c) 2004-2018 Department of Software Engineering - University of Szeged
 *
 *  Licensed under Version 1.2 of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence in the LICENSE file or at:
 *
 *  https://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 */

package columbus.java.asg.struc;

import columbus.java.asg.*;
import columbus.java.asg.base.Positioned;
import columbus.java.asg.base.PositionedWithoutComment;
import columbus.java.asg.base.Commentable;

/**
 * Interface CompilationUnit, which represents the {@link columbus.java.asg.struc.CompilationUnit CompilationUnit} node.
 * @columbus.node (missing)
 * @columbus.attr fileEncoding (String) : (missing)
 * @columbus.edge hasPackageDeclaration ({@link columbus.java.asg.struc.PackageDeclaration PackageDeclaration}, single) : (missing)
 * @columbus.edge hasImports ({@link columbus.java.asg.struc.Import Import}, multiple) : (missing)
 * @columbus.edge hasOthers ({@link columbus.java.asg.base.Positioned Positioned}, multiple) : (missing)
 * @columbus.edge hasModuleDeclaration ({@link columbus.java.asg.struc.ModuleDeclaration ModuleDeclaration}, single) : (missing)
 * @columbus.edge typeDeclarations ({@link columbus.java.asg.struc.TypeDeclaration TypeDeclaration}, multiple) : (missing)
 * @columbus.edge isInModule ({@link columbus.java.asg.struc.Module Module}, single) : (missing)
 */
public interface CompilationUnit extends PositionedWithoutComment, Commentable {

	/**
	 * Gives back the {@link columbus.java.asg.struc.CompilationUnit#attributeFileEncoding fileEncoding} of the node.
	 * @return Returns with the fileEncoding.
	 */
	public String getFileEncoding();

	/**
	 * Gives back the Key of {@link columbus.java.asg.struc.CompilationUnit#attributeFileEncoding fileEncoding} of the node.
	 * @return Returns with the Key of the fileEncoding.
	 */
	public int getFileEncodingKey();

	/**
	 * Sets the {@link columbus.java.asg.struc.CompilationUnit#attributeFileEncoding fileEncoding} of the node.
	 * @param value The new value of the fileEncoding.
	 */
	public void setFileEncoding(String value);

	/**
	 * Gives back the reference of the node the {@link columbus.java.asg.struc.CompilationUnit#edgeHasPackageDeclaration hasPackageDeclaration} edge points to.
	 * @return Returns the end point of the hasPackageDeclaration edge.
	 */
	public PackageDeclaration getPackageDeclaration();

	/**
	 * Sets the {@link columbus.java.asg.struc.CompilationUnit#edgeHasPackageDeclaration hasPackageDeclaration} edge.
	 * @param id The new end point of the hasPackageDeclaration edge.
	 */
	public void setPackageDeclaration(int id);

	/**
	 * Sets the {@link columbus.java.asg.struc.CompilationUnit#edgeHasPackageDeclaration hasPackageDeclaration} edge.
	 * @param node The new end point of the hasPackageDeclaration edge.
	 */
	public void setPackageDeclaration(PackageDeclaration node);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeHasPackageDeclaration hasPackageDeclaration} edge.
	 */
	public void removePackageDeclaration();

	/**
	 * Gives back iterator for the {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edges.
	 * @return Returns an iterator for the hasImports edges.
	 */
	public EdgeIterator<Import> getImportsIterator();

	/**
	 * Tells whether the node has {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edges or not.
	 * @return Returns true if the node doesn't have any hasImports edge.
	 */
	public boolean getImportsIsEmpty();

	/**
	 * Gives back how many {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edges the node has.
	 * @return Returns with the number of hasImports edges.
	 */
	public int getImportsSize();

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edge to the node.
	 * @param id The end point of the new hasImports edge.
	 */
	public void addImports(int id);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edge to the node.
	 * @param node The end point of the new hasImports edge.
	 */
	public void addImports(Import node);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edge to the node.
	 * @param node The end point of the new hasImports edge.
	 * @param index The index of end point of the new hasImports edge.
	 */
	public void addImports(Import node, int index);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edge to the node.
	 * @param node The end point of the new hasImports edge.
	 * @param index The index of end point of the new hasImports edge.
	 */
	public void setImports(Import node, int index);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edge by id from the node.
	 * @param id The new end point of the hasImports edge.
	 */
	public void removeImports(int id);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeHasImports hasImports} edge from the node.
	 * @param node The new end point of the hasImports edge.
	 */
	public void removeImports(Import node);

	/**
	 * Gives back iterator for the {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edges.
	 * @return Returns an iterator for the hasOthers edges.
	 */
	public EdgeIterator<Positioned> getOthersIterator();

	/**
	 * Tells whether the node has {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edges or not.
	 * @return Returns true if the node doesn't have any hasOthers edge.
	 */
	public boolean getOthersIsEmpty();

	/**
	 * Gives back how many {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edges the node has.
	 * @return Returns with the number of hasOthers edges.
	 */
	public int getOthersSize();

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edge to the node.
	 * @param id The end point of the new hasOthers edge.
	 */
	public void addOthers(int id);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edge to the node.
	 * @param node The end point of the new hasOthers edge.
	 */
	public void addOthers(Positioned node);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edge to the node.
	 * @param node The end point of the new hasOthers edge.
	 * @param index The index of end point of the new hasOthers edge.
	 */
	public void addOthers(Positioned node, int index);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edge to the node.
	 * @param node The end point of the new hasOthers edge.
	 * @param index The index of end point of the new hasOthers edge.
	 */
	public void setOthers(Positioned node, int index);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edge by id from the node.
	 * @param id The new end point of the hasOthers edge.
	 */
	public void removeOthers(int id);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeHasOthers hasOthers} edge from the node.
	 * @param node The new end point of the hasOthers edge.
	 */
	public void removeOthers(Positioned node);

	/**
	 * Gives back the reference of the node the {@link columbus.java.asg.struc.CompilationUnit#edgeHasModuleDeclaration hasModuleDeclaration} edge points to.
	 * @return Returns the end point of the hasModuleDeclaration edge.
	 */
	public ModuleDeclaration getModuleDeclaration();

	/**
	 * Sets the {@link columbus.java.asg.struc.CompilationUnit#edgeHasModuleDeclaration hasModuleDeclaration} edge.
	 * @param id The new end point of the hasModuleDeclaration edge.
	 */
	public void setModuleDeclaration(int id);

	/**
	 * Sets the {@link columbus.java.asg.struc.CompilationUnit#edgeHasModuleDeclaration hasModuleDeclaration} edge.
	 * @param node The new end point of the hasModuleDeclaration edge.
	 */
	public void setModuleDeclaration(ModuleDeclaration node);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeHasModuleDeclaration hasModuleDeclaration} edge.
	 */
	public void removeModuleDeclaration();

	/**
	 * Gives back iterator for the {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edges.
	 * @return Returns an iterator for the typeDeclarations edges.
	 */
	public EdgeIterator<TypeDeclaration> getTypeDeclarationsIterator();

	/**
	 * Tells whether the node has {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edges or not.
	 * @return Returns true if the node doesn't have any typeDeclarations edge.
	 */
	public boolean getTypeDeclarationsIsEmpty();

	/**
	 * Gives back how many {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edges the node has.
	 * @return Returns with the number of typeDeclarations edges.
	 */
	public int getTypeDeclarationsSize();

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edge to the node.
	 * @param id The end point of the new typeDeclarations edge.
	 */
	public void addTypeDeclarations(int id);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edge to the node.
	 * @param node The end point of the new typeDeclarations edge.
	 */
	public void addTypeDeclarations(TypeDeclaration node);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edge to the node.
	 * @param node The end point of the new typeDeclarations edge.
	 * @param index The index of end point of the new typeDeclarations edge.
	 */
	public void addTypeDeclarations(TypeDeclaration node, int index);

	/**
	 * Adds a new {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edge to the node.
	 * @param node The end point of the new typeDeclarations edge.
	 * @param index The index of end point of the new typeDeclarations edge.
	 */
	public void setTypeDeclarations(TypeDeclaration node, int index);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edge by id from the node.
	 * @param id The new end point of the typeDeclarations edge.
	 */
	public void removeTypeDeclarations(int id);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeTypeDeclarations typeDeclarations} edge from the node.
	 * @param node The new end point of the typeDeclarations edge.
	 */
	public void removeTypeDeclarations(TypeDeclaration node);

	/**
	 * Gives back the reference of the node the {@link columbus.java.asg.struc.CompilationUnit#edgeIsInModule isInModule} edge points to.
	 * @return Returns the end point of the isInModule edge.
	 */
	public Module getIsInModule();

	/**
	 * Sets the {@link columbus.java.asg.struc.CompilationUnit#edgeIsInModule isInModule} edge.
	 * @param id The new end point of the isInModule edge.
	 */
	public void setIsInModule(int id);

	/**
	 * Sets the {@link columbus.java.asg.struc.CompilationUnit#edgeIsInModule isInModule} edge.
	 * @param node The new end point of the isInModule edge.
	 */
	public void setIsInModule(Module node);

	/**
	 * Remove the {@link columbus.java.asg.struc.CompilationUnit#edgeIsInModule isInModule} edge.
	 */
	public void removeIsInModule();

}

